package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.PageWrapper
import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.comment.CommentDto
import com.wegdut.wegdut.data.like.LikeDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CommentDetailsApi {
    @GET("/comment/details")
    fun getComment(@Query("id") id: Long): Call<ResultWrapper<CommentDto>>

    @GET("/comment/liker")
    fun getLiker(
        @Query("id") id: Long, @Query("count") count: Int,
        @Query("offset") offset: Long, @Query("date") date: Long
    )
            : Call<ResultWrapper<PageWrapper<LikeDto>>>
}