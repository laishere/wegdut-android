package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.user.EmailRegisterDto
import com.wegdut.wegdut.data.user.StudentRegisterDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApi {
    @POST("/register")
    fun register(@Body dto: EmailRegisterDto): Call<ResultWrapper<Long?>>

    @POST("/register/student")
    fun registerStudent(@Body dto: StudentRegisterDto): Call<ResultWrapper<Long?>>
}