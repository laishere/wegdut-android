package com.wegdut.wegdut.ui.student_verification

import android.graphics.Bitmap
import com.wegdut.wegdut.ui.BasePresenter

class StudentVerificationContract {
    interface View {
        fun setGetCaptchaError(error: String?)
        fun setCaptcha(bitmap: Bitmap?)
        fun setVerificationError(error: String?)
        fun onStudentVerified()
        fun setSaveStudentInfoError(error: String?)
        fun onStudentInfoSaved()
        fun setSubmitEnabled(enabled: Boolean)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun getCaptcha(username: String)
        abstract fun verifyStudent(username: String, password: String, captcha: String?)
        abstract fun saveStudentInfo(username: String, password: String)
    }
}