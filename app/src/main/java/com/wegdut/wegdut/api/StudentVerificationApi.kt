package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.student_verification.StudentDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface StudentVerificationApi {
    @GET("http://authserver.gdut.edu.cn/authserver/login")
    fun loginPage(@Query("service") service: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST
    fun login(
        @Url url: String,
        @FieldMap data: Map<String, String>
    ): Call<ResponseBody>

    @GET("http://authserver.gdut.edu.cn/authserver/needCaptcha.html")
    fun checkNeedCaptcha(@Query("username") name: String): Call<String>

    @GET("http://authserver.gdut.edu.cn/authserver/captcha.html")
    fun getCaptcha(): Call<ResponseBody>

    @POST("/student-verification")
    fun saveStudentInfo(@Body dto: StudentDto): Call<ResultWrapper<String?>>

    @GET("/is-student-verified")
    fun isStudentVerified(): Call<ResultWrapper<Boolean>>
}