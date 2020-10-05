package com.wegdut.wegdut.scroll

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.LoadStatus
import com.wegdut.wegdut.helper.LazyUpdateHelper
import com.wegdut.wegdut.ui.BaseTypeRVAdapter
import com.wegdut.wegdut.utils.UIUtils
import javax.inject.Inject

open class ScrollView<T> @Inject constructor() : ScrollContract.View<T> {
    @Inject
    lateinit var presenter: ScrollContract.Presenter<T, ScrollView<T>>

    @Inject
    lateinit var adapter: ScrollAdapter<T>
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var recyclerView: RecyclerView
    var alwaysUpdate = true
    var loadMoreTrigger = 10
    private var items: List<T>? = null
    private var loadError: String? = null
    private var loadStatus: LoadStatus? = null
    private val lazyUpdateHelper = LazyUpdateHelper(::notifyDataChanged)

    private fun notifyDataChanged() {
        adapter.diff(merge())
    }

    private fun merge(): List<BaseTypeRVAdapter.Item> {
        val list = mutableListOf<BaseTypeRVAdapter.Item>()
        items?.let { a ->
            for (i in a)
                list.add(adapter.getItem(i))
        }
        loadStatus?.let {
            list.add(BaseTypeRVAdapter.Item(1, R.layout.item_bottom_loading, it))
        }
        loadError?.let {
            list.add(BaseTypeRVAdapter.Item(1, R.layout.item_bottom_error, it))
        }
        return list
    }

    private fun lazyUpdate() {
        lazyUpdateHelper.update()
    }

    override fun bind(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        UIUtils.setSwipeRefreshColor(swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recycler_view)
        swipeRefreshLayout.setOnRefreshListener { presenter.refresh() }
        val linearLayoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.layoutManager = linearLayoutManager
        adapter.alwaysUpdate = alwaysUpdate
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (loadError != null) return
                if (linearLayoutManager.findLastVisibleItemPosition() + loadMoreTrigger >= adapter.itemCount)
                    presenter.loadMore()
            }
        })
        presenter.subscribe(this)
    }

    override fun unbind() {
        presenter.unsubscribe()
    }

    override fun start() {
        presenter.start()
    }

    override fun stop() {
        presenter.stop()
    }

    override fun setItems(items: List<T>?) {
        this.items = items
        lazyUpdate()
    }

    override fun setRefreshing(refreshing: Boolean) {
        swipeRefreshLayout.isRefreshing = refreshing
    }

    override fun setLoadStatus(loadStatus: LoadStatus?) {
        this.loadStatus = loadStatus
        lazyUpdate()
    }

    override fun setError(error: String?) {
        loadError = error
        lazyUpdate()
    }
}