package com.wegdut.wegdut.ui.student_verification

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.student_verification.StudentVerificationDto
import com.wegdut.wegdut.data.student_verification.StudentVerificationRepository
import javax.inject.Inject

class StudentVerificationPresenter @Inject constructor() : StudentVerificationContract.Presenter() {

    @Inject
    lateinit var repository: StudentVerificationRepository

    override fun getCaptcha(username: String) {
        launch {
            view?.setSubmitEnabled(false)
            tryIt({
                val bitmap = io {
                    repository.getCaptcha(username)
                }
                view?.setCaptcha(bitmap)
                view?.setGetCaptchaError(null)
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setGetCaptchaError(err)
            }
            view?.setSubmitEnabled(true)
        }
    }

    override fun verifyStudent(username: String, password: String, captcha: String?) {
        launch {
            view?.setSubmitEnabled(false)
            tryIt({
                io {
                    repository.testLogin(username, password, captcha)
                }
                view?.setVerificationError(null)
                view?.onStudentVerified()
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setVerificationError(err)
                getCaptcha(username)
            }
            view?.setSubmitEnabled(true)
        }
    }

    override fun saveStudentInfo(username: String, password: String) {
        launch {
            view?.setSubmitEnabled(false)
            tryIt({
                io {
                    repository.saveStudentInfo(StudentVerificationDto(username, password))
                }
                view?.setSaveStudentInfoError(null)
                view?.onStudentInfoSaved()
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setSaveStudentInfoError(err)
            }
            view?.setSubmitEnabled(true)
        }
    }
}