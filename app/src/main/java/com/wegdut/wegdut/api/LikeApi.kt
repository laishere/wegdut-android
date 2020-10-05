package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.ResultWrapper
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface LikeApi {
    @POST("/like")
    fun like(
        @Query("type") type: ContentType, @Query("id") id: Long,
        @Query("like") like: Boolean
    ): Call<ResultWrapper<Boolean>>
}