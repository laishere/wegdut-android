package com.wegdut.wegdut.ui.email_register

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.api.RegisterApi
import com.wegdut.wegdut.data.user.EmailRegisterDto
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class EmailRegisterPresenter @Inject constructor() : EmailRegisterContract.Presenter() {
    @Inject
    lateinit var api: RegisterApi

    override fun register(
        username: String,
        nickname: String,
        password: String,
        password2: String,
        email: String
    ) {
        val valid = verifyUsername(username) &&
                verifyNickname(nickname) &&
                verifyPassword(password, password2) &&
                verifyEmail(email)
        if (!valid) return
        val dto = EmailRegisterDto(username, password, nickname, email)
        launch {
            view?.setSubmitEnabled(false)
            tryIt({
                io {
                    api.register(dto).extract()
                }
                view?.onRegisterSuccess()
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setRegisterError(err)
            }
            view?.setSubmitEnabled(true)
        }
    }

    private fun verifyUsername(text: String): Boolean {
        if (text.isBlank()) {
            view?.setUsernameError("用户名不能为空")
            return false
        }
        val ok = Regex("\\w{3,20}").matchEntire(text) != null
        if (!ok) view?.setUsernameError("用户名长度应在3到20位")
        return ok
    }

    private fun verifyNickname(text: String): Boolean {
        if (text.isBlank() || Regex("[\\s　]+").matchEntire(text) != null) {
            view?.setNicknameError("昵称不能为空")
            return false
        }
        return true
    }

    private fun verifyPassword(password: String, password2: String): Boolean {
        if (password.isBlank()) {
            view?.setPasswordError("密码不能为空")
            return false
        }
        val ok = Regex(".{6,}").matchEntire(password) != null
        if (!ok) {
            view?.setPasswordError("密码长度不应小于6位")
            return false
        }
        if (password2 != password) {
            view?.setPassword2Error("两次密码输入不一致")
            return false
        }
        return true
    }

    private fun verifyEmail(text: String): Boolean {
        if (text.isBlank()) {
            view?.setEmailError("电子邮箱地址不能为空")
            return false
        }
        val ok = Regex("\\S+@\\S+?\\.\\S{2,}").matchEntire(text) != null
        if (!ok) view?.setEmailError("电子邮箱地址格式不正确")
        return ok
    }
}