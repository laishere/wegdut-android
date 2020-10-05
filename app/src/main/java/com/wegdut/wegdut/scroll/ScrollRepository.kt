package com.wegdut.wegdut.scroll

interface ScrollRepository<T> {
    fun get(): List<T>
    fun loadMore()
    fun isEmpty(): Boolean
    fun hasMore(): Boolean
    fun refresh()
    val items: List<T>
}