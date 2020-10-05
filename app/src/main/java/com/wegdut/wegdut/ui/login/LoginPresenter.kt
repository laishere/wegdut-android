package com.wegdut.wegdut.ui.login

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.user.LoginRepository
import javax.inject.Inject

class LoginPresenter @Inject constructor() : LoginContract.Presenter() {

    @Inject
    lateinit var loginRepository: LoginRepository

    override fun login(username: String, password: String) {
        launch {
            view?.setSubmitEnabled(false)
            tryIt({
                io {
                    loginRepository.login(username, password)
                }
                view?.onLoginSuccess()
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setLoginError(err)
            }
            view?.setSubmitEnabled(true)
        }
    }
}