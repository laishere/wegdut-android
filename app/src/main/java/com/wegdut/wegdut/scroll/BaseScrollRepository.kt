package com.wegdut.wegdut.scroll

import com.wegdut.wegdut.data.PageWrapper
import com.wegdut.wegdut.utils.Utils.copy

abstract class BaseScrollRepository<T> : ScrollRepository<T> {
    private val cache = mutableListOf<T>()
    private var marker: String? = null
    private var isLast: Boolean = false
    protected val itemCountPerPage = 30
    override val items: List<T>
        get() = cache.copy()

    override fun get(): List<T> {
        if (cache.isEmpty() && hasMore())
            loadMore()
        return cache.copy()
    }

    override fun loadMore() {
        val page = get(itemCountPerPage, cache.size, marker)
        marker = page.marker
        isLast = page.isLast
        cache.addAll(page.list)
    }

    override fun isEmpty(): Boolean {
        return cache.isEmpty()
    }

    override fun hasMore(): Boolean {
        return !isLast
    }

    override fun refresh() {
        marker = null
        isLast = false
        cache.clear()
    }

    protected abstract fun get(count: Int, offset: Int, marker: String?): PageWrapper<T>
}