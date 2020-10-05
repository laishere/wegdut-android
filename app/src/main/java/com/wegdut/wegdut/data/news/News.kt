package com.wegdut.wegdut.data.news

import com.wegdut.wegdut.data.BaseDiffData
import java.util.*

data class News(
    override var id: Long,
    val category: String,
    val department: String,
    val title: String,
    val contentAbstract: String,
    val postTime: Date,
    val studentRelated: Boolean = false
) : BaseDiffData()