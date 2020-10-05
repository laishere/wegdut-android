package com.wegdut.wegdut.scroll

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.LoadStatus
import com.wegdut.wegdut.ui.BaseTypeRVAdapter

abstract class ScrollAdapter<T> : BaseTypeRVAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflate(parent, viewType)
        val vh = getViewHolder(viewType, view)
        if (vh != null) return vh
        return when (viewType) {
            R.layout.item_bottom_loading -> BottomLoadingViewHolder(view)
            R.layout.item_bottom_error -> BottomErrorViewHolder(view)
            else -> StaticViewHolder(view)
        }
    }

    class BottomLoadingViewHolder(itemView: View) : ViewHolder(itemView) {
        private val loading: View = itemView.findViewById(R.id.loading)
        private val done: View = itemView.findViewById(R.id.done)
        private val views = arrayOf(loading, done)
        override fun bind(data: Item) {
            val loadStatus = if (data.data is LoadStatus) data.data else return
            val i = when (loadStatus) {
                LoadStatus.LOADING -> 0
                else -> 1
            }
            for (v in views.withIndex())
                v.value.visibility = if (v.index == i) View.VISIBLE else View.GONE
        }
    }

    class BottomErrorViewHolder(itemView: View) : ViewHolder(itemView) {
        private val errorText: TextView = itemView.findViewById(R.id.text)
        override fun bind(data: Item) {
            val error = data.data as String
            errorText.text = error
        }
    }

    abstract fun getViewHolder(viewType: Int, view: View): ViewHolder?
    abstract fun getItem(data: T): Item
}