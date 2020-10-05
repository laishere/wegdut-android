package com.wegdut.wegdut.data.comment

import com.wegdut.wegdut.data.BaseDiffData
import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.data.user.User
import java.util.*

data class Comment(
    override var id: Long,
    val user: User,
    val postTime: Date,
    val content: String,
    val img: ImageItem? = null,
    val replyType: ContentType,
    var like: Int = 0,
    var comment: Int = 0,
    var iLike: Boolean = false,
    var iComment: Boolean = false,
    var iLikeAnimation: Boolean = false,
    var replyUser: User? = null
) : BaseDiffData() {
    fun getAbstract(): String {
        val imgAbstract = if (img != null) " [图片]" else ""
        return "$content$imgAbstract"
    }
}