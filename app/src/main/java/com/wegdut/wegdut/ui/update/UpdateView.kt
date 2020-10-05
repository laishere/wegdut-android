package com.wegdut.wegdut.ui.update

import android.app.Activity
import android.content.Intent
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wegdut.wegdut.BuildConfig
import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.config.Config
import com.wegdut.wegdut.data.app_update.UpdateRequest
import com.wegdut.wegdut.data.app_update.UpdateResponse
import com.wegdut.wegdut.dialog.LoadingDialog
import com.wegdut.wegdut.service.DownloadService
import com.wegdut.wegdut.service.UpdateService
import com.wegdut.wegdut.ui.BaseCoroutineModel
import com.wegdut.wegdut.utils.MessageUtils
import java.io.File

class UpdateView(private val activity: Activity) : BaseCoroutineModel() {

    private val updateService = UpdateService()
    private var updateResponse: UpdateResponse? = null
    private var checking = false
    private val loadingDialog = LoadingDialog(activity)
    private val downloadService = DownloadService()

    init {
        start()
    }

    fun checkUpdate(quiet: Boolean) {
        if (checking) return
        checking = true
        launch {
            tryIt({
                val rsp = io {
                    val req = UpdateRequest(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
                    updateService.checkUpdate(req)
                }
                if (!rsp.isLatest) {
                    updateResponse = rsp
                    if (!quiet || rsp.notifyUpdate || rsp.forceUpdate)
                        showUpdateDialog()
                } else if (!quiet) {
                    MessageUtils.info(activity, "已是最新版本")
                }
            }) {
                if (!quiet) {
                    val err = MyException.handle(it)
                    MessageUtils.info(activity, err)
                }
            }
        }
    }

    private fun showUpdateDialog() {
        val rsp = updateResponse ?: return
        val builder = MaterialAlertDialogBuilder(activity)
            .setTitle("有新版本")
            .setMessage(rsp.message)
            .setCancelable(false)
            .setPositiveButton("立即更新") { _, _ ->
                update()
            }
        if (!rsp.forceUpdate) {
            builder.setNegativeButton("暂不更新") { _, _ -> }
        }
        builder.show()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun update() {
        val url = updateResponse?.url ?: return
        loadingDialog.show()
        launch {
            tryIt({
                val file = io {
                    loadingDialog.action = "正在下载安装包"
                    val file = File.createTempFile("app_update", ".apk")
                    downloadService.download(url, file) {
                        loadingDialog.action = "正在下载 $it"
                    }
                    file
                }
                loadingDialog.successAndDismiss("下载完成") {
                    install(file)
                }
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                loadingDialog.errorAndDismiss(err)
            }
        }
    }

    private fun install(file: File) {
        val uri = FileProvider.getUriForFile(activity, Config.fileProviderAuthorities, file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        activity.startActivity(intent)
    }

    override fun stop() {
        super.stop()
        loadingDialog.dismiss()
    }
}