package com.wegdut.wegdut.ui.login

import com.wegdut.wegdut.ui.BasePresenter

class LoginContract {
    interface View {
        fun setLoginError(error: String)
        fun setSubmitEnabled(enabled: Boolean)
        fun onLoginSuccess()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun login(username: String, password: String)
    }
}