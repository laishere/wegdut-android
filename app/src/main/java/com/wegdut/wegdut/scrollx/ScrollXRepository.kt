package com.wegdut.wegdut.scrollx

interface ScrollXRepository<T> {
    fun refresh()
    fun isEmpty(): Boolean
    fun update(firstVisibleItem: Int, lastVisibleItem: Int): ScrollXUpdateAction
    fun doUpdate(): List<T>
    fun get(): List<T>
    fun hasMore(): Boolean
    fun updateAll()
    fun updateTop()
    fun delete(item: T)
    fun add(item: T, active: Boolean = true)
    fun add(index: Int, item: T, active: Boolean = true)
    fun update(item: T)
    val items: List<T>
}