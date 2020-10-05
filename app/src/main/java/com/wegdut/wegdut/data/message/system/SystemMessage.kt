package com.wegdut.wegdut.data.message.system

import com.wegdut.wegdut.data.BaseDiffData
import java.util.*

data class SystemMessage(
    override var id: Long,
    val uid: Long,
    val title: String,
    val message: String,
    var hasRead: Boolean,
    val createdAt: Date
) : BaseDiffData()