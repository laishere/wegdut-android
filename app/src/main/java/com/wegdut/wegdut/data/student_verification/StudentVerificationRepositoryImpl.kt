package com.wegdut.wegdut.data.student_verification

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.api.StudentVerificationApi
import com.wegdut.wegdut.utils.ApiUtils
import com.wegdut.wegdut.utils.ApiUtils.extract
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup
import javax.inject.Inject

class StudentVerificationRepositoryImpl @Inject constructor() : StudentVerificationRepository {

    @Inject
    lateinit var api: StudentVerificationApi

    override fun getCaptcha(username: String): Bitmap? {
        val needCaptcha = ApiUtils.handleCall(api.checkNeedCaptcha(username))
        if (needCaptcha == "false") return null
        val rsp = ApiUtils.handleCall(api.getCaptcha())
        return BitmapFactory.decodeStream(rsp.byteStream())
    }

    override fun testLogin(username: String, password: String, captcha: String?) {
        val pageRsp = api.loginPage(TEST_URL).execute()
        val page = pageRsp.body() ?: throw MyException("无法获取登录页面")
        val doc = Jsoup.parse(page.string())
        val form = doc.select("#casLoginForm")
        val inputs = form.select("input")
        val map = mutableMapOf<String, String>()
        for (i in inputs) {
            val n = i.attr("name")
            val v = i.attr("value")
            if (n.isNullOrBlank() || v == null) continue
            map[n] = v
        }
        map["username"] = username
        map["password"] = password
        captcha?.let { map["captchaResponse"] = captcha }
        val url = pageRsp.raw().request.url.resolve(form.attr("action"))
        val loginRsp = api.login(url.toString(), map).execute()
        val host = loginRsp.raw().request.url.host
        // 成功
        if (host == TEST_URL.toHttpUrl().host) return
        val errorDoc = Jsoup.parse(loginRsp.body()!!.string())
        val error = errorDoc.select("#msg").text()
        throw MyException(error)
    }

    override fun saveStudentInfo(dto: StudentVerificationDto) {
        api.saveStudentInfo(dto).extract()
    }

    override fun isVerified(): Boolean {
        return api.isStudentVerified().extract()
    }

    companion object {
        const val TEST_URL = "http://jxfw.gdut.edu.cn/new/ssoLogin"
    }
}