package com.wegdut.wegdut.ui.message.system

import android.view.View
import android.widget.TextView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.message.system.SystemMessage
import com.wegdut.wegdut.scroll.ScrollAdapter
import com.wegdut.wegdut.ui.ViewHolderHelper
import com.wegdut.wegdut.utils.DateUtils
import javax.inject.Inject

class SystemAdapter @Inject constructor() : ScrollAdapter<SystemMessage>() {

    class MessageViewHolder(itemView: View) : ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val content: TextView = itemView.findViewById(R.id.content)
        private val badge: View = itemView.findViewById(R.id.badge)

        override fun bind(data: Item) {
            val item = data.data as SystemMessage
            badge.visibility = if (item.hasRead) View.GONE else View.VISIBLE
            ViewHolderHelper.update(title, item.title)
            ViewHolderHelper.update(time, DateUtils.format(item.createdAt))
            ViewHolderHelper.update(content, item.message)
        }
    }

    override fun getViewHolder(viewType: Int, view: View): ViewHolder? {
        if (viewType == R.layout.item_message_system)
            return MessageViewHolder(view)
        return null
    }

    override fun getItem(data: SystemMessage): Item {
        return Item(data.id, R.layout.item_message_system, data)
    }
}