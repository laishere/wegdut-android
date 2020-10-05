package com.wegdut.wegdut.ui.email_register

import com.wegdut.wegdut.ui.BasePresenter

class EmailRegisterContract {
    interface View {
        fun setRegisterError(error: String)
        fun setSubmitEnabled(enabled: Boolean)
        fun onRegisterSuccess()
        fun setUsernameError(error: String)
        fun setPasswordError(error: String)
        fun setPassword2Error(error: String)
        fun setNicknameError(error: String)
        fun setEmailError(error: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun register(
            username: String,
            nickname: String,
            password: String,
            password2: String,
            email: String
        )
    }
}