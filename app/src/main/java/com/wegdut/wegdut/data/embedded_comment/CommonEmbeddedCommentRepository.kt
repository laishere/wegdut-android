package com.wegdut.wegdut.data.embedded_comment

import com.wegdut.wegdut.api.CommentApi
import com.wegdut.wegdut.api.LikeApi
import com.wegdut.wegdut.config.Config
import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.comment.Comment
import com.wegdut.wegdut.data.comment.ComplexComment
import com.wegdut.wegdut.data.comment.PostCommentDto
import com.wegdut.wegdut.data.scrollx.ScrollXRequest
import com.wegdut.wegdut.data.scrollx.ScrollXResponse
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

abstract class CommonEmbeddedCommentRepository<T> : EmbeddedCommentRepository<T>() {

    @Inject
    lateinit var commentApi: CommentApi

    @Inject
    lateinit var likeApi: LikeApi

    protected var contentId = 0L
    protected lateinit var contentType: ContentType

    override fun setContext(id: Long, type: ContentType) {
        contentId = id
        contentType = type
    }

    override fun reply(parent: ComplexComment, dto: PostCommentDto): List<ComplexComment> {
        val posted = commentApi.postComment(ContentType.COMMENT, parent.comment.id, dto).extract()
        parent.reply.add(posted.toComment())
        parent.replyCount++
        return items
    }

    override fun toggleLike(comment: Comment): List<ComplexComment> {
        val like = !comment.iLike
        likeApi.like(ContentType.COMMENT, comment.id, like).extract()
        comment.iLike = like
        comment.iLikeAnimation = true
        comment.like += if (like) 1 else -1
        return items
    }

    override fun loadMoreReplies(parent: ComplexComment): List<ComplexComment> {
        var lastReplyId: String? = null
        val replies = parent.reply
        val pos = parent.validCount - 1
        if (pos in 0 until replies.size)
            lastReplyId = replies[pos].id.toString()
        val page =
            commentApi.getReplies(parent.comment.id, lastReplyId, Config.commentReplyCountPerFetch)
                .extract()
        for (i in replies.size - 1 downTo parent.validCount)
            replies.removeLast()
        replies.addAll(page.list.map { it.toComment() })
        parent.validCount = replies.size
        return items
    }

    override fun getAnchor(item: ComplexComment?): String? {
        if (item == null) return null
        return item.comment.id.toString()
    }

    override fun getFromApi(request: ScrollXRequest): ScrollXResponse<ComplexComment> {
        val rsp = commentApi.getComment(contentType, contentId, request).extract()
        return rsp.map { it.toComplexComment() }
    }

    override fun canPostComment() {
        commentApi.canPost().extract()
    }

    override fun getFromLocal(limit: Int): List<ComplexComment> {
        return emptyList()
    }
}