package com.wegdut.wegdut.data.like

import com.wegdut.wegdut.data.user.UserDto
import java.util.*

data class LikeDto(
    val user: UserDto,
    val time: Date
) {
    fun toLike() = Like(user.toUser(), time)
}