package com.wegdut.wegdut.scroll

import com.wegdut.wegdut.data.LoadStatus
import com.wegdut.wegdut.ui.BasePresenter

class ScrollContract {
    interface View<T> {
        fun setItems(items: List<T>?)
        fun setRefreshing(refreshing: Boolean)
        fun setLoadStatus(loadStatus: LoadStatus?)
        fun setError(error: String?)
        fun start()
        fun stop()
        fun bind(view: android.view.View)
        fun unbind()
    }

    abstract class Presenter<T, V : View<T>> : BasePresenter<V>() {
        abstract fun refresh()
        abstract fun loadMore()
    }
}