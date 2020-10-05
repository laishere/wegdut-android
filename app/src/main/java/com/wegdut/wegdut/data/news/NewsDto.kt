package com.wegdut.wegdut.data.news

import java.util.*

data class NewsDto(
    var id: Long,
    var category: String,
    var department: String,
    var studentRelated: Boolean = false,
    var title: String,
    var contentAbstract: String,
    var postTime: Date
) {
    fun toNews() = News(
        id, category, department, title, contentAbstract, postTime, studentRelated
    )
}