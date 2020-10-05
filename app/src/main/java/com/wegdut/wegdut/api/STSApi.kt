package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.sts.STSDto
import retrofit2.Call
import retrofit2.http.GET

interface STSApi {
    @GET("/sts")
    fun getSTSToken(): Call<ResultWrapper<STSDto?>>
}