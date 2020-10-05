package com.wegdut.wegdut.data.message.like

import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.user.UserDto
import java.util.*

data class LikeMessageDto(
    val id: Long,
    val type: ContentType,
    val rid: Long,
    val userCount: Int,
    val originAbstract: String,
    val hasRead: Boolean,
    val createdAt: Date,
    var latestUsers: List<UserDto>
) {
    fun toLikeMessage() = LikeMessage(
        id,
        type,
        rid,
        userCount,
        originAbstract,
        hasRead,
        createdAt,
        latestUsers.map { it.toUser() }
    )
}