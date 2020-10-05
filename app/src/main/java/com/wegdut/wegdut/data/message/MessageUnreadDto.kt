package com.wegdut.wegdut.data.message

data class MessageUnreadDto(
    val like: Int,
    val reply: Int,
    val system: Int
) {
    fun toMessageUnread() = MessageUnread(like, reply, system)
}