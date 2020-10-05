package com.wegdut.wegdut.event

import com.wegdut.wegdut.data.message.MessageType


data class MessageAlreadyReadEvent(
    val type: MessageType
)