package com.wegdut.wegdut.ui.student_register

import com.wegdut.wegdut.ui.BasePresenter

class StudentRegisterContract {
    interface View {
        fun setError(error: String)
        fun onSuccess()
        fun setSubmitEnabled(enabled: Boolean)
        fun setUsernameError(error: String)
        fun setNicknameError(error: String)
        fun setPasswordError(error: String)
        fun setPassword2Error(error: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun register(
            username: String,
            nickname: String,
            password: String,
            password2: String,
            studentNumber: String,
            studentPassword: String
        )
    }
}