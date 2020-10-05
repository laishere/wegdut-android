package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.PageWrapper
import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.data.message.MessageUnreadDto
import com.wegdut.wegdut.data.message.like.LikeMessageDto
import com.wegdut.wegdut.data.message.reply.ReplyMessageDto
import com.wegdut.wegdut.data.message.system.SystemMessageDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MessageApi {
    @GET("/message/like")
    fun getLikeMessage(
        @Query("count") count: Int,
        @Query("offset") offset: Int,
        @Query("marker") marker: String?
    ): Call<ResultWrapper<PageWrapper<LikeMessageDto>>>

    @GET("/message/reply?v0.4")
    fun getReplyMessage(
        @Query("count") count: Int,
        @Query("offset") offset: Int,
        @Query("marker") marker: String?
    ): Call<ResultWrapper<PageWrapper<ReplyMessageDto>>>

    @GET("/message/system?v0.4")
    fun getSystemMessage(
        @Query("count") count: Int,
        @Query("offset") offset: Int,
        @Query("marker") marker: String?
    ): Call<ResultWrapper<PageWrapper<SystemMessageDto>>>

    @GET("/message/unread")
    fun getMessageUnread(): Call<ResultWrapper<MessageUnreadDto>>

    @POST("/message/read")
    fun readMessage(@Query("type") type: MessageType, @Query("id") id: Long)
            : Call<ResultWrapper<Boolean>>
}