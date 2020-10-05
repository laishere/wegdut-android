package com.wegdut.wegdut.data.post

import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.data.user.UserDto
import java.util.*

data class PostDto(
    var id: Long? = null,
    var content: String? = null,
    var images: List<ImageItem>? = null,
    var postTime: Date? = null,
    var like: Int = 0,
    var comment: Int = 0,
    var user: UserDto? = null,
    var iLike: Boolean = false,
    var iComment: Boolean = false
) {
    fun toPost() = Post(
        id!!, postTime!!, content!!, images!!, like, comment, user!!.toUser(), iLike, iComment
    )
}