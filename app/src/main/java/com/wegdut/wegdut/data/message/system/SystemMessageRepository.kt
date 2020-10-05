package com.wegdut.wegdut.data.message.system

import com.wegdut.wegdut.api.MessageApi
import com.wegdut.wegdut.data.PageWrapper
import com.wegdut.wegdut.data.message.MessageRepository
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class SystemMessageRepository @Inject constructor() : MessageRepository<SystemMessage>() {

    @Inject
    lateinit var messageApi: MessageApi

    override fun read(item: SystemMessage): List<SystemMessage> {
        messageApi.readMessage(MessageType.SYSTEM, item.id).extract()
        item.hasRead = true
        return items
    }

    override fun get(count: Int, offset: Int, marker: String?): PageWrapper<SystemMessage> {
        return messageApi.getSystemMessage(count, offset, marker).extract()
            .map { it.toSystemMessage() }
    }
}