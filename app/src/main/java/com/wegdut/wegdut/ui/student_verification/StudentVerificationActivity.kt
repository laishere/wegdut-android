package com.wegdut.wegdut.ui.student_verification

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.student_register.StudentRegisterActivity
import com.wegdut.wegdut.utils.UIUtils
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class StudentVerificationActivity : DaggerAppCompatActivity(), StudentVerificationContract.View {

    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var errorText: TextView
    private lateinit var captchaWrapper: ViewGroup
    private lateinit var captchaInput: TextInputEditText
    private lateinit var captchaImg: ImageView
    private lateinit var submitBtn: Button
    private var isRegister = false

    @Inject
    lateinit var presenter: StudentVerificationPresenter
    private var getCaptchaError: String? = null
    private var verificationError: String? = null
    private var saveStudentInfoError: String? = null

    companion object {
        const val REGISTER = "REGISTER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_verification)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        isRegister = intent.getBooleanExtra(REGISTER, isRegister)
        initView()
        presenter.start()
        presenter.subscribe(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        oldBitmap?.recycle()
    }

    private fun initView() {
        usernameInput = findViewById(R.id.name)
        passwordInput = findViewById(R.id.password)
        errorText = findViewById(R.id.error)
        captchaWrapper = findViewById(R.id.captcha_wrapper)
        captchaInput = findViewById(R.id.captcha)
        captchaImg = findViewById(R.id.captcha_img)
        submitBtn = findViewById(R.id.submit)

        usernameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) getCaptcha()
        }
        submitBtn.setOnClickListener { submit() }
    }

    private fun getCaptcha() {
        if (!verifyUsername()) return
        presenter.getCaptcha(usernameInput.text?.toString() ?: "")
    }

    private var oldBitmap: Bitmap? = null

    private fun submit() {
        if (verifyUsername()
            && verifyPassword()
            && verifyCaptcha()
        ) {
            verifyStudent()
        }
    }

    private fun verifyStudent() {
        UIUtils.hideKeyboard(this)
        val name = usernameInput.text.toString()
        val password = passwordInput.text.toString()
        val captcha = if (captchaWrapper.visibility == View.GONE) null
        else captchaInput.text.toString()
        presenter.verifyStudent(name, password, captcha)
    }


    private fun startRegister() {
        val intent = Intent(this, StudentRegisterActivity::class.java)
        intent.putExtra(StudentRegisterActivity.STUDENT_NUMBER, usernameInput.text.toString())
        intent.putExtra(StudentRegisterActivity.STUDENT_PASSWORD, passwordInput.text.toString())
        startActivity(intent)
        finish()
    }

    private fun saveStudentInfo() {
        presenter.saveStudentInfo(usernameInput.text.toString(), passwordInput.text.toString())
    }

    private fun verifyUsername(): Boolean {
        val text = usernameInput.text?.toString() ?: return false
        if (text.isBlank()) {
            usernameInput.error = "学号不能为空"
            return false
        }
        return true
    }

    private fun verifyPassword(): Boolean {
        val text = passwordInput.text?.toString() ?: return false
        if (text.isBlank()) {
            passwordInput.error = "密码不能为空"
            return false
        }
        return true
    }

    private fun verifyCaptcha(): Boolean {
        if (captchaWrapper.visibility == View.GONE) return true
        val text = captchaInput.text?.toString() ?: return false
        if (text.isBlank()) {
            captchaInput.error = "验证码不能为空"
            return false
        }
        return true
    }

    override fun onStudentVerified() {
        errorText.visibility = View.GONE
        if (isRegister) startRegister()
        else saveStudentInfo()
    }

    private fun updateError() {
        val error = saveStudentInfoError ?: verificationError ?: getCaptchaError
        if (error == null) {
            errorText.visibility = View.GONE
            return
        }
        errorText.text = error
        errorText.visibility = View.VISIBLE
    }

    override fun setGetCaptchaError(error: String?) {
        getCaptchaError = error
        updateError()
    }

    override fun setCaptcha(bitmap: Bitmap?) {
        oldBitmap?.recycle()
        if (bitmap == null) {
            captchaWrapper.visibility = View.GONE
            return
        }
        captchaWrapper.visibility = View.VISIBLE
        captchaImg.setImageBitmap(bitmap)
        oldBitmap = bitmap
    }

    override fun setVerificationError(error: String?) {
        verificationError = error
        updateError()
    }

    override fun setSaveStudentInfoError(error: String?) {
        saveStudentInfoError = error
        updateError()
    }

    override fun onStudentInfoSaved() {
        supportFinishAfterTransition()
    }

    override fun setSubmitEnabled(enabled: Boolean) {
        submitBtn.isEnabled = enabled
    }
}