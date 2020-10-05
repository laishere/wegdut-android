package com.wegdut.wegdut.data.message

import com.wegdut.wegdut.scroll.BaseScrollRepository

abstract class MessageRepository<T> : BaseScrollRepository<T>() {
    abstract fun read(item: T): List<T>
}