package com.wegdut.wegdut.data.message.reply

import com.wegdut.wegdut.data.comment.PostCommentDto
import com.wegdut.wegdut.data.message.MessageRepository

abstract class ReplyMessageRepository : MessageRepository<ReplyMessage>() {
    abstract fun reply(item: ReplyMessage, dto: PostCommentDto): List<ReplyMessage>
    abstract fun toggleLike(item: ReplyMessage): List<ReplyMessage>
}
