package com.wegdut.wegdut.data.remote_storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadRequest
import com.alibaba.sdk.android.oss.model.InitiateMultipartUploadRequest
import com.alibaba.sdk.android.oss.model.PartETag
import com.alibaba.sdk.android.oss.model.UploadPartRequest
import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.api.STSApi
import com.wegdut.wegdut.config.Config
import com.wegdut.wegdut.utils.ApiUtils
import okhttp3.internal.closeQuietly
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*
import javax.inject.Inject
import kotlin.math.min

class OSSRepository @Inject constructor() : StorageRepository {
    @Inject
    lateinit var api: STSApi

    @Inject
    lateinit var context: Context

    private var ossClientInstance: OSSClient? = null
    private val ossClient: OSSClient
        get() {
            if (ossClientInstance == null)
                initClient()
            return ossClientInstance!!
        }

    override fun uploadImage(image: Uri, quality: Int, sizeLimit: Int): String {
        return putImage(image, quality, sizeLimit)
    }

    private fun initClient() {
        val sts = ApiUtils.handleResultWrapper(api.getSTSToken())!!
        val credentialProvider =
            OSSStsTokenCredentialProvider(sts.accessKeyId, sts.accessKeySecret, sts.securityToken)
        ossClientInstance = OSSClient(context, Config.ossEndpoint, credentialProvider)
    }

    private fun putImage(uri: Uri, quality: Int = 70, sizeLimit: Int = 1080): String {
        return openUri(uri, Config.ossImagePrefix) { name, ext, ins ->
            var fixedExt = ext
            val format = when (ext) {
                "jpg", "webp", "png" -> {
                    fixedExt = "jpg"
                    Bitmap.CompressFormat.JPEG
                }
                else -> null
            }
            val objectName = fullName(name, fixedExt)
            val newIns =
                if (format != null) compressImage(ins, format, quality, sizeLimit)
                else ins
            MyLog.debug(this, "上传文件 $objectName")
            putFile(objectName, newIns)
            newIns.closeQuietly()
            objectName
        }
    }

    private fun compressImage(
        ins: InputStream,
        format: Bitmap.CompressFormat,
        quality: Int,
        sizeLimit: Int
    ): InputStream {
        val rawData = ins.readBytes()
        ins.closeQuietly()
        val raw = BitmapFactory.decodeByteArray(rawData, 0, rawData.size)
        val rawStream = ByteArrayInputStream(rawData)
        val exif = ExifInterface(rawStream)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val orientationFixed =
            if (orientation == ExifInterface.ORIENTATION_NORMAL) raw
            else {
                val matrix = Matrix()
                val w = raw.width
                val h = raw.height
                val cx = w / 2f
                val cy = h / 2f
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f, cx, cy)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f, cx, cy)
                    ExifInterface.ORIENTATION_TRANSPOSE -> {
                        matrix.setScale(-1f, 1f, cx, cy)
                        matrix.setRotate(270f)
                    }
                    ExifInterface.ORIENTATION_TRANSVERSE -> {
                        matrix.setScale(-1f, 1f, cx, cy)
                        matrix.setRotate(90f)
                    }
                }
                Bitmap.createBitmap(raw, 0, 0, w, h, matrix, true)
            }
        var w = orientationFixed.width
        var h = orientationFixed.height
        if (min(w, h) > sizeLimit) {
            if (w < h) {
                h = h * sizeLimit / w
                w = sizeLimit
            } else {
                w = w * sizeLimit / h
                h = sizeLimit
            }
        }
        MyLog.debug(this, "压缩图片 ${orientationFixed.width} x ${orientationFixed.height} -> $w $h")
        val new = orientationFixed.scale(w, h)
        val outs = ByteArrayOutputStream()
        new.compress(format, quality, outs)
        raw.recycle()
        orientationFixed.recycle()
        new.recycle()
        return ByteArrayInputStream(outs.toByteArray())
    }

    private fun openUri(
        uri: Uri,
        prefix: String,
        read: (String, String, InputStream) -> String
    ): String {
        val contentResolver = context.contentResolver
        var type = contentResolver.getType(uri)
        if (type == "image/jpg") type = "image/jpeg"
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(type) ?: ""
        val name = prefix + getName()
        val ins = contentResolver.openInputStream(uri)
            ?: throw MyException("无法读取Uri数据")
        var err: Throwable? = null
        var fullName = ""
        try {
            fullName = read(name, ext, ins)
        } catch (e: ClientException) {
            err = MyException("上传本地错误", e)
        } catch (e: ServiceException) {
            err = MyException("上传服务器错误", e)
        } catch (e: Throwable) {
            err = e
        }
        ins.closeQuietly()
        err?.let { throw it }
        return fullName
    }

    private fun fullName(name: String, ext: String): String {
        if (ext.isNotBlank()) return "$name.$ext"
        return name
    }

    /**
     * 由客户端指定文件名直接传到oss，可能会被攻击，例如，把用户正常文件替换覆盖成同名其它文件
     */
    private fun putFile(objectName: String, ins: InputStream) {
        val bucketName = Config.ossBucketName
        val init = InitiateMultipartUploadRequest(Config.ossBucketName, objectName)
        val initResult = ossClient.initMultipartUpload(init)
        val uploadId = initResult.uploadId
        var partNumber = 1
        val eTags = mutableListOf<PartETag>()
        val chunkSize = 1000 * 1024
        val buf = ByteArray(chunkSize)
        while (true) {
            val size = ins.read(buf)
            if (size < 0) break
            val req = UploadPartRequest()
            req.bucketName = bucketName
            req.objectKey = objectName
            req.uploadId = uploadId
            req.partNumber = partNumber++
            val content =
                if (size < buf.size) buf.copyOf(size)
                else buf
            req.partContent = content
            val partResult = ossClient.uploadPart(req)
            eTags.add(PartETag(req.partNumber, partResult.eTag))
        }
        val complete = CompleteMultipartUploadRequest(bucketName, objectName, uploadId, eTags)
        ossClient.completeMultipartUpload(complete)
    }

    private fun getName(): String {
        return UUID.randomUUID().toString()
    }
}
