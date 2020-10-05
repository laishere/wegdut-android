package com.wegdut.wegdut.data.message.reply

import com.wegdut.wegdut.data.comment.Comment
import java.util.*

data class ReplyMessage(
    val id: Long,
    val comment: Comment,
    val originAbstract: String,
    var hasRead: Boolean,
    val createdAt: Date
)