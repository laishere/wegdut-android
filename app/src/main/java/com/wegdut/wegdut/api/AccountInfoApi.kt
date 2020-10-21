package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.user.StudentDto
import com.wegdut.wegdut.data.user.UserDto
import com.wegdut.wegdut.data.user.UserModificationDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AccountInfoApi {
    @POST("/user/modify")
    fun modifyUser(@Body dto: UserModificationDto): Call<ResultWrapper<UserDto>>

    @GET("/user/student")
    fun getStudent(): Call<ResultWrapper<StudentDto>>
}