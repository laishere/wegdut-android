package com.wegdut.wegdut.data.message.reply

import com.wegdut.wegdut.api.CommentApi
import com.wegdut.wegdut.api.LikeApi
import com.wegdut.wegdut.api.MessageApi
import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.PageWrapper
import com.wegdut.wegdut.data.comment.PostCommentDto
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class ReplyMessageRepositoryImpl @Inject constructor() : ReplyMessageRepository() {

    @Inject
    lateinit var messageApi: MessageApi

    @Inject
    lateinit var commentApi: CommentApi

    @Inject
    lateinit var likeApi: LikeApi

    override fun reply(item: ReplyMessage, dto: PostCommentDto): List<ReplyMessage> {
        val c = item.comment
        commentApi.postComment(ContentType.COMMENT, c.id, dto).extract()
        c.iComment = true
        c.comment++
        return items
    }

    override fun toggleLike(item: ReplyMessage): List<ReplyMessage> {
        val c = item.comment
        val like = !c.iLike
        likeApi.like(ContentType.COMMENT, c.id, like).extract()
        c.iLike = like
        c.like += if (like) 1 else -1
        c.iLikeAnimation = true
        return items
    }

    override fun read(item: ReplyMessage): List<ReplyMessage> {
        messageApi.readMessage(MessageType.REPLY, item.id).extract()
        item.hasRead = true
        return items
    }

    override fun get(count: Int, offset: Int, marker: String?): PageWrapper<ReplyMessage> {
        return messageApi.getReplyMessage(count, offset, marker).extract()
            .map { it.toReplyMessage() }
    }
}