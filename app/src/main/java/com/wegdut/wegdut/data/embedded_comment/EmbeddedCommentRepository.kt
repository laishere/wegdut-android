package com.wegdut.wegdut.data.embedded_comment

import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.comment.Comment
import com.wegdut.wegdut.data.comment.ComplexComment
import com.wegdut.wegdut.data.comment.PostCommentDto
import com.wegdut.wegdut.scrollx.BaseScrollXRepository

abstract class EmbeddedCommentRepository<T> : BaseScrollXRepository<ComplexComment>() {
    abstract fun setContext(id: Long, type: ContentType)
    abstract fun getContent(): T
    abstract fun reply(parent: ComplexComment, dto: PostCommentDto): List<ComplexComment>
    abstract fun reply(parent: T, dto: PostCommentDto): List<ComplexComment>
    abstract fun toggleLike(comment: Comment): List<ComplexComment>
    abstract fun toggleLike(content: T)
    abstract fun loadMoreReplies(parent: ComplexComment): List<ComplexComment>
    abstract fun canPostComment()
}