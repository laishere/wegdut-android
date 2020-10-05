package com.wegdut.wegdut.scroll

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.LoadStatus
import javax.inject.Inject

open class ScrollPresenter<T, V : ScrollContract.View<T>> @Inject constructor() :
    ScrollContract.Presenter<T, V>() {
    @Inject
    lateinit var repository: ScrollRepository<T>
    open var clearItemsAfterRefreshingFailed = false
    private var busy = false
    private var isRefreshing = false
        set(value) {
            field = value
            view?.setRefreshing(value)
        }

    override fun refresh() {
        if (busy) {
            isRefreshing = false
            return
        }
        jobs.clear()
        repository.refresh()
        get(false)
    }

    override fun loadMore() {
        if (repository.hasMore())
            get(true)
    }

    private fun get(more: Boolean) {
        if (busy) return
        launch {
            busy = true
            view?.setError(null)
            if (repository.isEmpty()) {
                isRefreshing = true
                view?.setLoadStatus(null)
            } else if (more)
                view?.setLoadStatus(LoadStatus.LOADING)
            tryIt({
                val items = io {
                    if (more) repository.loadMore()
                    repository.get()
                }
                view?.setItems(items)
                if (!repository.hasMore())
                    view?.setLoadStatus(LoadStatus.DONE)
            }) {
                MyLog.error(this, it)
                if (isRefreshing && clearItemsAfterRefreshingFailed)
                    view?.setItems(null)
                val err = MyException.handle(it)
                view?.setError(err)
                view?.setLoadStatus(null)
            }
            isRefreshing = false
            busy = false
        }
    }

    override fun start() {
        super.start()
        busy = false
    }

    override fun subscribe(view: V) {
        super.subscribe(view)
        get(false)
    }
}