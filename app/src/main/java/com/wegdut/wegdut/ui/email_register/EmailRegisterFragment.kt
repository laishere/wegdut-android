package com.wegdut.wegdut.ui.email_register

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.BaseDaggerFragment
import com.wegdut.wegdut.utils.MessageUtils
import javax.inject.Inject

class EmailRegisterFragment : BaseDaggerFragment(R.layout.fragment_email_register),
    EmailRegisterContract.View {
    private lateinit var userNameInput: TextInputEditText
    private lateinit var nicknameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var password2Input: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var submitBtn: Button

    @Inject
    lateinit var presenter: EmailRegisterPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        presenter.subscribe(this)
    }

    override fun onDestroyView() {
        presenter.unsubscribe()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.start()
    }

    override fun onDestroy() {
        presenter.stop()
        super.onDestroy()
    }

    private fun initView(v: View) {
        userNameInput = v.findViewById(R.id.name)
        nicknameInput = v.findViewById(R.id.nickname)
        passwordInput = v.findViewById(R.id.password)
        password2Input = v.findViewById(R.id.password2)
        emailInput = v.findViewById(R.id.email)
        submitBtn = v.findViewById(R.id.btn_submit)

        submitBtn.setOnClickListener {
            submit()
        }

        val loginBtn: View = v.findViewById(R.id.btn_login)
        loginBtn.setOnClickListener { startLogin() }
    }

    private fun startLogin() {
        activity?.let {
            val act = it as EmailRegisterActivity
            act.startLogin()
        }
    }

    private fun submit() {
        register()
    }

    private fun register() {
        presenter.register(
            userNameInput.text?.toString() ?: "",
            nicknameInput.text?.toString() ?: "",
            passwordInput.text?.toString() ?: "",
            password2Input.text?.toString() ?: "",
            emailInput.text?.toString() ?: ""
        )
    }

    override fun setRegisterError(error: String) {
        MessageUtils.info(context, error)
    }

    override fun setSubmitEnabled(enabled: Boolean) {
        submitBtn.isEnabled = enabled
    }

    override fun onRegisterSuccess() {
        activity?.let {
            val act = it as EmailRegisterActivity
            act.startRegisterResult()
        }
    }

    override fun setUsernameError(error: String) {
        userNameInput.error = error
    }

    override fun setPasswordError(error: String) {
        passwordInput.error = error
    }

    override fun setPassword2Error(error: String) {
        password2Input.error = error
    }

    override fun setNicknameError(error: String) {
        nicknameInput.error = error
    }

    override fun setEmailError(error: String) {
        emailInput.error = error
    }
}