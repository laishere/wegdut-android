package com.wegdut.wegdut.data.message.like

import com.wegdut.wegdut.api.MessageApi
import com.wegdut.wegdut.data.PageWrapper
import com.wegdut.wegdut.data.message.MessageRepository
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class LikeMessageRepository @Inject constructor() : MessageRepository<LikeMessage>() {

    @Inject
    lateinit var messageApi: MessageApi

    override fun read(item: LikeMessage): List<LikeMessage> {
        messageApi.readMessage(MessageType.LIKE, item.id).extract()
        item.hasRead = true
        return items
    }

    override fun get(count: Int, offset: Int, marker: String?): PageWrapper<LikeMessage> {
        return messageApi.getLikeMessage(count, offset, marker).extract().map { it.toLikeMessage() }
    }
}