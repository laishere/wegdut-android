package com.wegdut.wegdut.ui.news

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.news.News
import com.wegdut.wegdut.scroll.ScrollAdapter
import com.wegdut.wegdut.utils.DateUtils
import javax.inject.Inject

class NewsAdapter @Inject constructor() : ScrollAdapter<News>() {

    class NotificationViewHolder(itemView: View) : ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val content: TextView = itemView.findViewById(R.id.content)
        private val department: TextView = itemView.findViewById(R.id.department)
        private val time: TextView = itemView.findViewById(R.id.time)

        @SuppressLint("SetTextI18n")
        override fun bind(data: Item) {
            val item = data.data as News
            title.text = item.title
            content.text = item.contentAbstract
            department.text = item.department
            time.text = DateUtils.format(item.postTime)
        }
    }

    override fun getViewHolder(viewType: Int, view: View): ViewHolder? {
        if (viewType == R.layout.item_news)
            return NotificationViewHolder(view)
        return null
    }

    override fun getItem(data: News): Item {
        return Item(data.id, R.layout.item_news, data)
    }

}