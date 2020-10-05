package com.wegdut.wegdut.data.comment

import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.data.user.UserDto
import java.util.*

data class CommentDto(
    val id: Long,
    val content: String,
    val images: List<ImageItem>?,
    val like: Int,
    val comment: Int,
    val user: UserDto,
    val replyUser: UserDto?,
    val level: Int = 0,
    val iComment: Boolean = false,
    val iLike: Boolean = false,
    val comments: List<CommentDto>? = null,
    val postTime: Date,
    val type: ContentType
) {
    fun toComment(): Comment {
        val img = images?.firstOrNull()
        return Comment(
            id,
            user.toUser(),
            postTime,
            content,
            img,
            type,
            like,
            comment,
            iLike,
            iComment,
            replyUser = replyUser?.toUser()
        )
    }

    fun toComplexComment(): ComplexComment {
        val replies = comments?.map { it.toComment() } ?: listOf()
        return ComplexComment(toComment(), replies.toMutableList(), comment, replies.size)
    }

    companion object {
        const val BLOG_COMMENT = 1
        const val ARTICLE_COMMENT = 2
        const val COMMENT_REPLY = 3
    }
}