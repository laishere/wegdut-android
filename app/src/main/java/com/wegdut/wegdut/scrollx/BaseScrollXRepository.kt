package com.wegdut.wegdut.scrollx

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.scrollx.ScrollXRequest
import com.wegdut.wegdut.data.scrollx.ScrollXResponse
import com.wegdut.wegdut.utils.Utils.copy
import kotlin.math.max
import kotlin.math.min

abstract class BaseScrollXRepository<T> : ScrollXRepository<T> {

    protected open var pageItemCount = 30
    protected open var maxPageItemCount = 60
    protected open var ignoreDurationAfterDone = 60_000L // 加载触底后暂停继续往下加载60s
    protected open var localCacheLimit = 300

    private var activeStart = -1
    private var activeEnd = -1
    private var topAnchor = 0
    private var bottomAnchor = 0
    private var topUpdateCount = 0
    private var bottomUpdateCount = 0
    private val updateThreshold
        get() = pageItemCount / 4
    protected var cache = mutableListOf<T>()
    private var lastTopDoneTime = 0L
    private var lastBottomDoneTime = 0L
    private var updateId = 0
    private var hasMore = true

    override val items: List<T>
        get() = synchronized(cache) {
            cache.copy()
        }

    override fun refresh() {
        synchronized(cache) {
            cache.clear()
            hasMore = true
            activeStart = 0
            activeEnd = 0
            lastTopDoneTime = 0L
            lastBottomDoneTime = 0L
        }
    }

    override fun isEmpty(): Boolean {
        synchronized(cache) {
            return cache.isEmpty()
        }
    }

    override fun update(firstVisibleItem: Int, lastVisibleItem: Int): ScrollXUpdateAction {
        val visibleCount = lastVisibleItem - firstVisibleItem
        pageItemCount = max(pageItemCount, visibleCount)
        if (activeStart < 0) {
            activeStart = max(0, firstVisibleItem)
            activeEnd = activeStart
        }
        val now = System.currentTimeMillis()
        topAnchor = max(activeStart, firstVisibleItem)
        topUpdateCount =
            if (now - lastTopDoneTime < ignoreDurationAfterDone) 0
            else if (topAnchor - activeStart < updateThreshold) {
                if (activeStart - maxPageItemCount >= 0)
                    maxPageItemCount
                else pageItemCount
            } else 0
        bottomAnchor = min(activeEnd - 1, lastVisibleItem)
        bottomAnchor = max(topAnchor, bottomAnchor)
        bottomUpdateCount =
            if (now - lastBottomDoneTime < ignoreDurationAfterDone) 0
            else if (activeEnd - bottomAnchor < updateThreshold) {
                if (maxPageItemCount + activeEnd <= cache.size)
                    maxPageItemCount
                else pageItemCount
            } else 0
        val action = when {
            topUpdateCount == 0 && bottomUpdateCount == 0 -> ScrollXUpdateAction.NOTHING
            activeEnd + bottomUpdateCount > cache.size -> ScrollXUpdateAction.LOAD_TO_BOTTOM
            activeStart - topUpdateCount < 0 -> ScrollXUpdateAction.LOAD_TO_TOP
            else -> ScrollXUpdateAction.UPDATE
        }
        if (action != ScrollXUpdateAction.NOTHING) {
            MyLog.debug(this, "$action $activeStart $activeEnd $topUpdateCount $bottomUpdateCount")
        }
        return action
    }

    private data class UpdateEnvironment(
        val updateId: Int,
        val activeStart: Int,
        val activeEnd: Int
    )

    override fun doUpdate(): List<T> {
        val req: ScrollXRequest
        val currentEnvironment = synchronized(cache) {
            req = ScrollXRequest(
                getAnchor(topAnchor),
                topAnchor + 1 - activeStart,
                topUpdateCount,
                getAnchor(bottomAnchor),
                if (activeEnd > activeStart) activeEnd - bottomAnchor else 0,
                bottomUpdateCount
            )

            updateId++
            // updateId 用于多个更新发生时，只保留最后的更新
            // 当activeStart和activeEnd更改时，数据更新可能会引发异常，应该抛弃更新
            UpdateEnvironment(updateId, activeStart, activeEnd)

        }
        val rsp = getFromApi(req)
        synchronized(cache) {
            val newEnvironment = UpdateEnvironment(updateId, activeStart, activeEnd)
            if (currentEnvironment != newEnvironment)
                throw MyException("数据更新已过期")

            // 开始更新数据
            val newCache = mutableListOf<T>()
            val a = activeStart - req.topCount
            val b = activeEnd + req.bottomCount
            if (!rsp.isTopDone) {
                for (i in 0 until a)
                    newCache.add(cache[i])
            }

            // active 部分
            val newActiveStart = newCache.size
            newCache.addAll(rsp.topList)
            for (i in max(0, activeStart) until min(cache.size, activeEnd))
                newCache.add(cache[i])
            newCache.addAll(rsp.bottomList)
            activeStart = newActiveStart
            activeEnd = newCache.size

            if (!rsp.isBottomDone) {
                for (i in b until cache.size)
                    newCache.add(cache[i])
            }

            cache = newCache
        }

        if (rsp.isTopDone) lastTopDoneTime = System.currentTimeMillis()
        if (rsp.isBottomDone) lastBottomDoneTime = System.currentTimeMillis()
        hasMore = !rsp.isBottomDone
        return items
    }

    override fun hasMore(): Boolean {
        return hasMore
    }

    override fun get(): List<T> {
        if (cache.isEmpty())
            synchronized(cache) {
                cache.addAll(getFromLocal(localCacheLimit))
            }
        if (cache.isEmpty()) {
            update(0, 0)
            doUpdate()
        }
        return items
    }

    override fun updateTop() {
        lastTopDoneTime = 0L
    }

    override fun updateAll() {
        activeStart = -1
        lastTopDoneTime = 0L
        lastBottomDoneTime = 0L
    }

    override fun delete(item: T) {
        synchronized(cache) {
            val pos = cache.indexOf(item)
            if (pos >= 0) {
                cache.removeAt(pos)
                if (activeStart > pos)
                    activeStart--
                if (activeEnd > pos)
                    activeEnd--
            }
        }
    }

    override fun add(item: T, active: Boolean) {
        synchronized(cache) {
            cache.add(item)
            if (active && activeEnd == cache.size - 1)
                activeEnd = cache.size
        }
    }

    override fun add(index: Int, item: T, active: Boolean) {
        synchronized(cache) {
            cache.add(index, item)
            if (activeStart >= index) activeStart++
            if (activeEnd >= index) activeEnd++
            if (active) {
                if (activeStart == index + 1)
                    activeStart = index
                if (activeEnd == index - 1)
                    activeEnd = index
            }
        }
    }

    override fun update(item: T) {
        synchronized(cache) {
            val pos = getItemOldPosition(item)
            if (pos < 0) return
            cache[pos] = item
        }
    }

    private fun getAnchor(pos: Int): String? {
        if (pos < 0 || pos >= cache.size)
            return null
        return getAnchor(cache[pos])
    }

    protected abstract fun getAnchor(item: T?): String?
    protected open fun getItemOldPosition(item: T): Int = -1
    protected abstract fun getFromApi(request: ScrollXRequest): ScrollXResponse<T>
    protected abstract fun getFromLocal(limit: Int): List<T>
}