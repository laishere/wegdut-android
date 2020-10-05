package com.wegdut.wegdut.data.post

import com.wegdut.wegdut.data.BaseDiffData
import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.data.user.User
import java.util.*

data class Post(
    override var id: Long,
    val postTime: Date,
    val content: String,
    val images: List<ImageItem>,
    var like: Int,
    var comment: Int,
    val user: User,
    var iLike: Boolean = false,
    var iComment: Boolean = false,
    var iLikeAnimation: Boolean = false,
    var iCommentAnimation: Boolean = false
) : BaseDiffData() {
    fun getAbstract(): String {
        val imageAbstract = when {
            images.isEmpty() -> ""
            images.size == 1 -> " [图片]"
            else -> " [图片] x ${images.size}"
        }
        return "$content$imageAbstract"
    }
}