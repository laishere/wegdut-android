package com.wegdut.wegdut.dialog

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.ListRVAdapter

class SimpleOptionListDialog(context: Context, private val title: CharSequence? = null) :
    BottomListDialog(context) {
    var options: List<String>? = null
        set(value) {
            field = value
            adapter.items = value
            adapter.notifyDataSetChanged()
        }
    var active = -1
        set(value) {
            field = value
            adapter.updateActive(value)
        }

    private val adapter = Adapter()
    var onOptionSelectedListener: OnOptionSelectedListener? = null

    init {
        adapter.onItemClickListener = object : ListRVAdapter.OnItemClickListener {
            override fun onClick(view: View, pos: Int) {
                active = pos
                onOptionSelectedListener?.onSelected(pos)
            }
        }
    }

    interface OnOptionSelectedListener {
        fun onSelected(position: Int)
    }

    override fun getAdapter(): RecyclerView.Adapter<*> {
        return adapter
    }

    override fun getTitle(): CharSequence? {
        return title
    }

    override fun onInit() {}

    class Adapter : ListRVAdapter<String, ViewHolder>() {
        private var lastActive = -1

        fun updateActive(active: Int) {
            if (lastActive == active) return
            val old = lastActive
            lastActive = active
            if (old in 0..itemCount) notifyItemChanged(old, 1)
            if (active in 0..itemCount) notifyItemChanged(active, 1)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SimpleOptionListDialog.ViewHolder {
            val v = inflate(parent, viewType)
            return SimpleOptionListDialog.ViewHolder(v)
        }

        override fun onBindViewHolder(holder: SimpleOptionListDialog.ViewHolder, position: Int) {
            holder.active = lastActive
            super.onBindViewHolder(holder, position)
        }

        override fun getItemViewType(position: Int): Int {
            return R.layout.item_simple_option
        }
    }

    class ViewHolder(itemView: View) : ListRVAdapter.ViewHolder<String>(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.text)
        var active = -1
        override fun bind(data: String) {
            itemView.isActivated = active == adapterPosition
            textView.text = data
        }
    }
}