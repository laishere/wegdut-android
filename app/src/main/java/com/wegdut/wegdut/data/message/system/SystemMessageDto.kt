package com.wegdut.wegdut.data.message.system

import java.util.*

data class SystemMessageDto(
    val id: Long,
    val uid: Long,
    val title: String,
    val message: String,
    val hasRead: Boolean,
    val createdAt: Date
) {
    fun toSystemMessage() = SystemMessage(
        id, uid, title, message, hasRead, createdAt
    )
}