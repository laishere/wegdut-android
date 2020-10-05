package com.wegdut.wegdut.data.message.reply

import com.wegdut.wegdut.data.comment.CommentDto
import java.util.*

data class ReplyMessageDto(
    val id: Long,
    val comment: CommentDto,
    val originAbstract: String,
    val hasRead: Boolean,
    val createdAt: Date
) {
    fun toReplyMessage() = ReplyMessage(id, comment.toComment(), originAbstract, hasRead, createdAt)
}