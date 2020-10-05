package com.wegdut.wegdut.scrollx

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

open class ScrollXView<T> @Inject constructor() : ScrollXContract.View<T> {

    protected lateinit var swipeRefreshLayout: SwipeRefreshLayout
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var layoutManager: LinearLayoutManager

    @Inject
    lateinit var presenter: ScrollXContract.Presenter<T, ScrollXContract.View<T>>

    @Inject
    lateinit var adapter: ScrollXAdapter<T>
    private var fixUpdateTimes = 0
    protected val lazyRVUpdateHelper = LazyUpdateHelper(::updateRV)
    private var isRefreshing = false
    private val lazyRefreshStateUpdateHelper = LazyUpdateHelper {
        swipeRefreshLayout.isRefreshing = isRefreshing
    }.apply { delay = 200 }
    private var alwaysUpdate = true
    private var items: List<T>? = null
    private var loadStatus: LoadStatus? = null
    private var loadingError: String? = null

    override fun bind(view: View) {
        onBindSetup(view)
        onStart()
    }

    override fun unbind() {
        onStop()
    }

    override fun start() {
        presenter.start()
    }

    override fun stop() {
        presenter.stop()
    }

    override fun updateVisibleItems() {
        val first = layoutManager.findFirstVisibleItemPosition()
        if (first == RecyclerView.NO_POSITION) return
        val last = layoutManager.findLastVisibleItemPosition()
        adapter.notifyItemRangeChanged(first, last - first + 1, 1)
    }

    override fun requestUpdate() {
        val a = layoutManager.findFirstVisibleItemPosition()
        val b = layoutManager.findLastVisibleItemPosition()
        presenter.update(a, b)
    }

    fun updateAll() {
        presenter.updateAll()
    }

    fun updateTop() {
        presenter.updateTop()
    }

    protected open fun onBindSetup(v: View) {
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh)
        recyclerView = v.findViewById(R.id.recycler_view)
        layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        adapter.alwaysUpdate = alwaysUpdate
        recyclerView.addOnScrollListener(scrollListener)
        swipeRefreshLayout.setOnRefreshListener { presenter.refresh() }
        UIUtils.setSwipeRefreshColor(swipeRefreshLayout)
    }

    protected open fun onStart() {
        presenter.subscribe(this)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            requestUpdate()
        }
    }

    protected open fun onStop() {
        presenter.unsubscribe()
    }

    private fun updateRV() {
        adapter.diff(merge())
    }

    private fun merge(): List<BaseTypeRVAdapter.Item> {
        val list = mutableListOf<BaseTypeRVAdapter.Item>()
        onMerge(list)
        return list
    }

    protected open fun onMerge(list: MutableList<BaseTypeRVAdapter.Item>) {
        items?.let { a ->
            list.addAll(a.map { adapter.getItem(it) })
        }
        loadStatus?.let {
            list.add(BaseTypeRVAdapter.Item(1, R.layout.item_bottom_loading, it))
        }
        loadingError?.let {
            list.add(BaseTypeRVAdapter.Item(1, R.layout.item_bottom_error, it))
        }
    }

    override fun setItems(items: List<T>) {
        this.items = items
        lazyRVUpdateHelper.update()
    }

    override fun setRefreshing(refreshing: Boolean) {
        isRefreshing = refreshing
        lazyRefreshStateUpdateHelper.update()
    }

    override fun setLoadStatus(status: LoadStatus?) {
        if (loadStatus == status) return
        this.loadStatus = status
        lazyRVUpdateHelper.update()
    }

    override fun setLoadingError(error: String?) {
        loadingError = error
        lazyRVUpdateHelper.update()
    }
}