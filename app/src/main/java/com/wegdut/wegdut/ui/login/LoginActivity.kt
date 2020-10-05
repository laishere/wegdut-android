package com.wegdut.wegdut.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.email_register.EmailRegisterActivity
import com.wegdut.wegdut.ui.student_verification.StudentVerificationActivity
import com.wegdut.wegdut.utils.MessageUtils
import com.wegdut.wegdut.utils.UIUtils
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity(), LoginContract.View {

    private var dialog: BottomSheetDialog? = null
    private lateinit var submitBtn: Button

    @Inject
    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        val name: TextInputEditText = findViewById(R.id.name)
        val password: TextInputEditText = findViewById(R.id.password)
        val register: View = findViewById(R.id.btn_register)
        register.setOnClickListener { showRegisterOptions() }
        submitBtn = findViewById(R.id.submit)
        submitBtn.setOnClickListener {
            val n = name.text?.toString()
            val p = password.text?.toString()
            when {
                n.isNullOrBlank() -> name.error = "用户名不能为空"
                p.isNullOrBlank() -> password.error = "密码不能为空"
                else -> presenter.login(n, p)
            }
        }
        presenter.start()
        presenter.subscribe(this)
    }

    override fun onDestroy() {
        dialog?.dismiss()
        presenter.unsubscribe()
        presenter.stop()
        super.onDestroy()
    }

    private fun startEmailRegister() {
        val intent = Intent(this, EmailRegisterActivity::class.java)
        startActivity(intent)
        dialog?.dismiss()
    }

    private fun startStudentRegister() {
        val intent = Intent(this, StudentVerificationActivity::class.java)
        intent.putExtra(StudentVerificationActivity.REGISTER, true)
        startActivity(intent)
        dialog?.dismiss()
    }

    private fun showRegisterOptions() {
        val dialog = BottomSheetDialog(this, R.style.BottomOptionDialog)
        dialog.setContentView(R.layout.dialog_register_option)
        dialog.show()
        dialog.behavior
        val studentBtn: View = dialog.findViewById(R.id.student)!!
        val emailBtn: View = dialog.findViewById(R.id.email)!!
        studentBtn.setOnClickListener {
            startStudentRegister()
        }
        emailBtn.setOnClickListener {
            startEmailRegister()
        }
        this.dialog = dialog
    }

    override fun onLoginSuccess() {
        supportFinishAfterTransition()
    }

    override fun setLoginError(error: String) {
        MessageUtils.info(this, error)
    }

    override fun setSubmitEnabled(enabled: Boolean) {
        submitBtn.isEnabled = enabled
    }
}