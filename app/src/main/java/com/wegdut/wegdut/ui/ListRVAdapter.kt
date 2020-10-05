package com.wegdut.wegdut.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wegdut.wegdut.utils.MyDiffUtils
import java.util.concurrent.LinkedBlockingQueue

abstract class ListRVAdapter<T, VH : ListRVAdapter.ViewHolder<T>>(open var items: List<T>? = null) :
    RecyclerView.Adapter<VH>() {
    private var shouldSetItemsQuietly = false
    var onItemClickListener: OnItemClickListener? = null
    protected val queue = LinkedBlockingQueue<List<T>>()
    var diffCallback: MyDiffUtils.SimpleDiffCallback<T>? = null

    fun diff(new: List<T>) {
        queue.put(new)
        handleQueue()
    }

    protected open fun handleQueue() {
        val new = queue.poll() ?: return
        MyDiffUtils.diffToListRVAdapter(new, this, diffCallback = diffCallback) {
            handleQueue()
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onItemClickListener?.let {
            holder.itemView.setOnClickListener { v ->
                it.onClick(v, holder.adapterPosition)
            }
        }
        holder.bind(items!![position])
    }

    abstract class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(data: T)
    }

    interface OnItemClickListener {
        fun onClick(view: View, pos: Int)
    }

    protected companion object {
        @JvmStatic
        fun inflate(parent: ViewGroup, layout: Int): View {
            return LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        }
    }

    fun setItemsQuietly(items: List<T>) {
        shouldSetItemsQuietly = true
        this.items = items
        shouldSetItemsQuietly = false
    }
}