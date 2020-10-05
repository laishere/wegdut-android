package com.wegdut.wegdut.ui.send_post

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import com.wegdut.wegdut.ui.ListRVAdapter
import java.util.*

open class ImageHelperCallback<T>(private val adapter: ListRVAdapter<T, out ListRVAdapter.ViewHolder<T>>) :
    ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val all = LEFT or RIGHT or UP or DOWN
        return makeFlag(ACTION_STATE_DRAG, all) or
                makeFlag(ACTION_STATE_SWIPE, LEFT or RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition
        val items = adapter.items!!.toMutableList()
        if (from < to) for (i in from until to)
            Collections.swap(items, i, i + 1)
        else for (i in from downTo to + 1)
            Collections.swap(items, i, i - 1)
        adapter.setItemsQuietly(items)
        adapter.notifyItemMoved(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val i = viewHolder.adapterPosition
        val items = adapter.items!!.toMutableList()
        items.removeAt(i)
        adapter.setItemsQuietly(items)
        adapter.notifyItemRemoved(i)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }
}