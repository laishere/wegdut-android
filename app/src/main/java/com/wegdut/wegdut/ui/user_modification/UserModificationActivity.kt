package com.wegdut.wegdut.ui.user_modification

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.wegdut.wegdut.MyApplication
import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.R
import com.wegdut.wegdut.config.Config
import com.wegdut.wegdut.data.remote_storage.StorageRepository
import com.wegdut.wegdut.data.user.AccountInfoRepository
import com.wegdut.wegdut.data.user.UserModificationDto
import com.wegdut.wegdut.dialog.LoadingDialog
import com.wegdut.wegdut.glide.GlideApp
import com.wegdut.wegdut.ui.Jobs
import com.wegdut.wegdut.utils.MessageUtils
import com.wegdut.wegdut.utils.PermissionUtils
import com.wegdut.wegdut.utils.UIUtils
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserModificationActivity : DaggerAppCompatActivity() {

    private lateinit var icon: ImageView
    private lateinit var nicknameInput: TextInputEditText
    private lateinit var submitBtn: Button
    private var iconUri: Uri? = null
    private val loadingDialog = LoadingDialog(this)

    @Inject
    lateinit var accountInfoRepository: AccountInfoRepository

    @Inject
    lateinit var storageRepository: StorageRepository

    private val jobs = Jobs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_modification)
        initView()
    }

    override fun onDestroy() {
        loadingDialog.dismiss()
        jobs.clear()
        super.onDestroy()
    }

    private fun initView() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        icon = findViewById(R.id.icon)
        nicknameInput = findViewById(R.id.nickname)
        submitBtn = findViewById(R.id.submit)
        submitBtn.setOnClickListener {
            submit()
        }
        icon.setOnClickListener { pickImage() }
        val user = MyApplication.user!!
        updateView(Uri.parse(user.icon), user.nickname)
    }

    private fun updateView(iconUri: Uri?, nickname: String) {
        if (iconUri != null)
            GlideApp.with(icon)
                .load(iconUri)
                .placeholder(R.drawable.shape_rect_image_placeholder)
                .into(icon)
        nicknameInput.setText(nickname)
    }

    private fun pickImage() {
        PermissionUtils.pickImage(this, "需要存储权限哦") {
            Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .imageEngine(GlideEngine())
                .capture(true)
                .captureStrategy(CaptureStrategy(false, Config.fileProviderAuthorities))
                .forResult(1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 1 || resultCode != Activity.RESULT_OK) return
        iconUri = Matisse.obtainResult(data).firstOrNull()
        updateView(iconUri, nicknameInput.text.toString())
    }

    private fun submit() {
        if (!verify()) return
        submitBtn.isEnabled = false
        val nickname = nicknameInput.text?.toString()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    loadingDialog.show()
                    var icon: String? = null
                    iconUri?.let {
                        loadingDialog.action = "正在上传图片"
                        icon = storageRepository.uploadImage(it, sizeLimit = 512)
                    }
                    loadingDialog.action = "正在提交修改"
                    val dto = UserModificationDto(icon, nickname)
                    accountInfoRepository.modify(dto)

                }
                loadingDialog.successAndDismiss("修改成功") {
                    supportFinishAfterTransition()
                }
            } catch (e: Throwable) {
                val err = MyException.handle(e)
                loadingDialog.errorAndDismiss(err)
                MyLog.error(this, e)
            }
            submitBtn.isEnabled = true
        }
    }

    private fun verify(): Boolean {
        val nickname = nicknameInput.text
        if (nickname.isNullOrBlank()) {
            err("昵称不能为空")
            return false
        }
        val user = MyApplication.user
        if (user?.nickname == nickname.toString() && iconUri == null) {
            err("没有更改")
            return false
        }
        return true
    }

    private fun err(err: String) {
        MessageUtils.info(this, err)
    }
}