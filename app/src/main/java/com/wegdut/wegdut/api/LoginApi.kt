package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.user.UserDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApi {
    @POST("/login")
    fun login(@Query("name") name: String, @Query("password") password: String):
            Call<ResultWrapper<UserDto>>

    @POST("/logout")
    fun logout(): Call<ResponseBody>

    @GET("/current-user")
    fun getCurrentUser(): Call<ResultWrapper<UserDto?>>
}