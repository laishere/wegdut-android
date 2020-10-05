package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.post.PostDto
import com.wegdut.wegdut.data.scrollx.ScrollXRequest
import com.wegdut.wegdut.data.scrollx.ScrollXResponse
import com.wegdut.wegdut.data.send_post.SendPostDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PostApi {
    @POST("post")
    fun sendPost(@Body post: SendPostDto): Call<ResultWrapper<PostDto?>>

    @POST("post/latest")
    fun getLatest(@Body request: ScrollXRequest): Call<ResultWrapper<ScrollXResponse<PostDto>?>>

    @POST("post/delete")
    fun delete(@Query("id") id: Long): Call<ResultWrapper<Long?>>

    @POST("post/can_send_post")
    fun canSendPost(): Call<ResultWrapper<Boolean?>>

    @GET("blog/details")
    fun getPost(@Query("id") id: Long): Call<ResultWrapper<PostDto>>
}