package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.PageWrapper
import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.comment.CommentDto
import com.wegdut.wegdut.data.comment.PostCommentDto
import com.wegdut.wegdut.data.scrollx.ScrollXRequest
import com.wegdut.wegdut.data.scrollx.ScrollXResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CommentApi {
    @POST("/comment/list?v0.4")
    fun getComment(
        @Query("type") type: ContentType,
        @Query("id") id: Long,
        @Body request: ScrollXRequest
    ): Call<ResultWrapper<ScrollXResponse<CommentDto>>>

    @GET("/comment/reply")
    fun getReplies(
        @Query("branch") branch: Long,
        @Query("anchor") anchor: String?,
        @Query("count") count: Int
    ):
            Call<ResultWrapper<PageWrapper<CommentDto>>>

    @GET("/comment/list")
    fun getComment(
        @Query("type") type: ContentType,
        @Query("id") id: Long,
        @Query("count") count: Int,
        @Query("offset") offset: Long,
        @Query("marker") marker: String?
    ): Call<ResultWrapper<PageWrapper<CommentDto>>>

    @POST("/comment")
    fun postComment(
        @Query("type") type: ContentType, @Query("id") id: Long,
        @Body comment: PostCommentDto
    ): Call<ResultWrapper<CommentDto>>

    @POST("/comment/can_post")
    fun canPost(): Call<ResultWrapper<Boolean?>>
}