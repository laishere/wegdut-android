package com.wegdut.wegdut.data.message.like

import com.wegdut.wegdut.data.BaseDiffData
import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.user.User
import java.util.*

data class LikeMessage(
    override var id: Long,
    val type: ContentType,
    val rid: Long,
    val userCount: Int,
    val originAbstract: String,
    var hasRead: Boolean,
    val createdAt: Date,
    var latestUsers: List<User>
) : BaseDiffData()