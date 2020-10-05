package com.wegdut.wegdut.utils

import com.wegdut.wegdut.data.ContentType

object Utils {

    fun <T> List<T>.copy() = map { it }

    fun ContentType.toText(): String {
        return when (this) {
            ContentType.POST -> "动态"
            ContentType.COMMENT -> "评论"
            ContentType.ARTICLE -> "文章"
        }
    }

    fun <T> quiet(block: () -> T) = try {
        block()
    } catch (e: Throwable) {
        null
    }
}