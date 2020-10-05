package com.wegdut.wegdut.ui.news

import android.app.Activity
import android.content.Intent
import android.view.View
import com.wegdut.wegdut.data.news.News
import com.wegdut.wegdut.scroll.ScrollView
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.ui.Reselectable
import com.wegdut.wegdut.ui.news_details.NewsDetailsActivity
import javax.inject.Inject

class NewsView @Inject constructor() : ScrollView<News>(), Reselectable {
    fun bind(view: View, activity: Activity) {
        bind(view)
        adapter.onItemClickListener = object : ListRVAdapter.OnItemClickListener {
            override fun onClick(view: View, pos: Int) {
                val item = adapter.items?.get(pos) ?: return
                if (item.data is News) {
                    val news = item.data
                    val intent = Intent(activity, NewsDetailsActivity::class.java)
                    intent.putExtra(NewsDetailsActivity.ID, news.id)
                    intent.putExtra(NewsDetailsActivity.TITLE, news.category)
                    activity.startActivity(intent)
                }
            }
        }
    }

    override fun reselect() {
        recyclerView.scrollToPosition(0)
    }
}