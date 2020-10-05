package com.wegdut.wegdut.ui

import android.view.View
import com.wegdut.wegdut.data.BaseDiffData
import com.wegdut.wegdut.utils.MyDiffUtils

abstract class BaseTypeRVAdapter :
    ListRVAdapter<BaseTypeRVAdapter.Item, BaseTypeRVAdapter.ViewHolder>() {
    abstract class ViewHolder(itemView: View) : ListRVAdapter.ViewHolder<Item>(itemView)

    var alwaysUpdate = true

    class StaticViewHolder(itemView: View) : ViewHolder(itemView) {
        override fun bind(data: Item) {}
    }

    override fun handleQueue() {
        val new = queue.poll() ?: return
        MyDiffUtils.diffToBaseTypeRVAdapter(new, this, alwaysUpdate = alwaysUpdate) {
            handleQueue()
        }
    }

    data class Item(
        override var id: Long,
        val type: Int,
        val data: Any? = null
    ) : BaseDiffData() {
        override fun isSame(other: BaseDiffData): Boolean {
            return other is Item &&
                    id == other.id && type == other.type
        }

        override fun hasSameContent(other: BaseDiffData): Boolean {
            return other is Item &&
                    type == other.type && data == other.data
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items!![position].type
    }
}