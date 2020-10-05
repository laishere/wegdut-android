package com.wegdut.wegdut.scrollx

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.LoadStatus
import javax.inject.Inject

open class ScrollXPresenter<T, V : ScrollXContract.View<T>> @Inject constructor() :
    ScrollXContract.Presenter<T, V>() {

    @Inject
    lateinit var scrollXRepository: ScrollXRepository<T>
    private var updating = false
    protected var loadingError: String? = null
        set(value) {
            field = value
            view?.setLoadingError(loadingError)
        }
    protected var refreshing = false
        set(value) {
            field = value
            view?.setRefreshing(value)
        }

    override fun refresh() {
        jobs.clear()
        refreshing = true
        scrollXRepository.refresh()
        update(0, 0)
    }

    override fun update(firstVisibleItem: Int, lastVisibleItem: Int) {
        if (updating) return
        val action = scrollXRepository.update(firstVisibleItem, lastVisibleItem)
        if (action == ScrollXUpdateAction.NOTHING) {
            refreshing = false
            return
        }
        updating = true
        // 刷新时不改变加载状态
        if (!refreshing) {
            // 有错误时，隐藏底部加载状态，只有在未加载完毕且是往后加载时才显示LOADING， 其它操作不主动更改加载状态
            if (loadingError != null)
                view?.setLoadStatus(null)
            else if (scrollXRepository.hasMore() && action == ScrollXUpdateAction.LOAD_TO_BOTTOM)
                view?.setLoadStatus(LoadStatus.LOADING)
        }
        launch {
            tryIt({
                val items = io {
                    scrollXRepository.doUpdate()
                }
                view?.setItems(items)
                if (!scrollXRepository.hasMore())
                    view?.setLoadStatus(LoadStatus.DONE)
                loadingError = null
            }) {
                MyLog.error(this, it)
                loadingError = MyException.handle(it)
                view?.setLoadStatus(null)
            }
            refreshing = false
            updating = false
        }
    }

    private fun get() {
        updating = true
        view?.setRefreshing(true)
        loadingError = null
        view?.setLoadStatus(null)
        launch {
            tryIt({
                val items = io {
                    scrollXRepository.get()
                }
                view?.setItems(items)
                if (!scrollXRepository.hasMore())
                    view?.setLoadStatus(LoadStatus.DONE)
            })
            updating = false
            view?.setRefreshing(false)
            update(0, 0)
        }
    }

    override fun updateAll() {
        scrollXRepository.updateAll()
        view?.requestUpdate()
    }

    override fun updateTop() {
        scrollXRepository.updateTop()
        view?.requestUpdate()
    }

    override fun subscribe(view: V) {
        super.subscribe(view)
        get()
    }

    override fun start() {
        super.start()
        updating = false
    }
}