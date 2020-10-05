package com.wegdut.wegdut.service

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.utils.ApiUtils
import okhttp3.Request
import okhttp3.internal.closeQuietly
import okhttp3.internal.headersContentLength
import java.io.File

class DownloadService {

    fun download(url: String, file: File, chunkSize: Int = 1024, onProgress: (String) -> Unit) {
        val req = Request.Builder()
            .get()
            .url(url)
            .build()
        val call = ApiUtils.okHttpClient.newCall(req)
        val rsp = call.execute()
        if (!rsp.isSuccessful)
            throw MyException(ApiUtils.getHttpError(rsp.code))
        val contentLength = rsp.headersContentLength()
        val ins = rsp.body!!.byteStream()
        val outs = file.outputStream()
        val buf = ByteArray(chunkSize)
        var totalRead = 0L
        var lastProgress = ""
        while (true) {
            val count = ins.read(buf)
            if (count < 0) break
            outs.write(buf, 0, count)
            totalRead += count
            val progress =
                if (contentLength < 0) format(totalRead)
                else "%.1f%%".format(totalRead.toFloat() * 100f / contentLength)
            if (lastProgress != progress) {
                onProgress(progress)
                lastProgress = progress
            }
        }
        ins.closeQuietly()
        rsp.closeQuietly()
        outs.closeQuietly()
    }

    companion object {
        const val K = 1024L
        const val M = K * K
        const val G = K * M

        fun format(size: Long): String {
            return when {
                size < K -> "%dB".format(size)
                size < M -> "%dKB".format(size / K)
                size < G -> "%.1fMB".format(size.toFloat() / M)
                else -> "%.2fGB".format(size.toFloat() / G)
            }
        }
    }
}