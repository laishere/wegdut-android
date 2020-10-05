package com.wegdut.wegdut.data.like

import com.wegdut.wegdut.data.user.User
import java.util.*

data class Like(
    val user: User,
    val time: Date
)