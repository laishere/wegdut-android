package com.wegdut.wegdut.scrollx

import com.wegdut.wegdut.data.LoadStatus
import com.wegdut.wegdut.ui.BasePresenter

class ScrollXContract {
    interface View<T> {
        fun setItems(items: List<T>)
        fun setRefreshing(refreshing: Boolean)
        fun setLoadStatus(status: LoadStatus?)
        fun setLoadingError(error: String?)
        fun bind(view: android.view.View)
        fun updateVisibleItems()
        fun unbind()
        fun start()
        fun stop()
        fun requestUpdate()
    }

    abstract class Presenter<T, V : View<T>> : BasePresenter<V>() {
        abstract fun refresh()
        abstract fun update(firstVisibleItem: Int, lastVisibleItem: Int)
        abstract fun updateAll()
        abstract fun updateTop()
    }
}