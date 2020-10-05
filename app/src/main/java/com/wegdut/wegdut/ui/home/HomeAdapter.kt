package com.wegdut.wegdut.ui.home

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.home.course.HomeCourse
import com.wegdut.wegdut.data.news.News
import com.wegdut.wegdut.ui.BaseTypeRVAdapter
import com.wegdut.wegdut.ui.ViewHolderHelper
import com.wegdut.wegdut.utils.DateUtils
import com.wegdut.wegdut.view.CourseWrapper

class HomeAdapter : BaseTypeRVAdapter() {

    var newsHeadListener: NewsHeadListener? = null
    var courseListener: CourseListener? = null
    var onNewsItemClickListener: OnNewsItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflate(parent, viewType)
        return when (viewType) {
            R.layout.item_home_course_wrapper -> CourseViewHolder(view)
            R.layout.item_home_news_error -> TextViewHolder(view)
            R.layout.item_home_news -> NewsViewHolder(view)
            R.layout.item_home_news_head -> NewsHeadViewHolder(view)
            else -> StaticViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is NewsHeadViewHolder -> holder.newsHeadListener = newsHeadListener
            is CourseViewHolder -> holder.courseListener = courseListener
            is NewsViewHolder -> holder.onNewsItemClickListener = onNewsItemClickListener
        }
        super.onBindViewHolder(holder, position)
    }

    class CourseViewHolder(itemView: View) : ViewHolder(itemView) {
        private val wrapper: CourseWrapper = itemView.findViewById(R.id.course_wrapper)
        private val courseBtn: View = itemView.findViewById(R.id.btn_course)
        var courseListener: CourseListener? = null
        override fun bind(data: Item) {
            val item = data.data as HomeCourseData
            ViewHolderHelper.update(wrapper, item) { _, _ ->
                wrapper.homeCourseData = item
            }
            courseBtn.setOnClickListener { courseListener?.onClick() }
        }
    }

    class TextViewHolder(itemView: View) : ViewHolder(itemView) {
        private val text: TextView = itemView.findViewById(R.id.text)
        override fun bind(data: Item) {
            ViewHolderHelper.update(text, data.data as String)
        }
    }

    class NewsViewHolder(itemView: View) : ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val content: TextView = itemView.findViewById(R.id.content)
        private val department: TextView = itemView.findViewById(R.id.department)
        private val time: TextView = itemView.findViewById(R.id.time)
        var onNewsItemClickListener: OnNewsItemClickListener? = null
        override fun bind(data: Item) {
            val item = data.data as News
            itemView.setOnClickListener { onNewsItemClickListener?.onClick(item) }
            ViewHolderHelper.update(title, item.title)
            ViewHolderHelper.update(content, item.contentAbstract)
            ViewHolderHelper.update(department, item.department)
            ViewHolderHelper.update(time, DateUtils.format(item.postTime))
        }
    }

    class NewsHeadViewHolder(itemView: View) : ViewHolder(itemView) {
        private val switchBtn: SwitchMaterial = itemView.findViewById(R.id.switch_btn)
        private val newsBtn: View = itemView.findViewById(R.id.btn_news)
        var newsHeadListener: NewsHeadListener? = null
        override fun bind(data: Item) {
            switchBtn.isChecked = data.data as Boolean
            newsBtn.setOnClickListener { newsHeadListener?.onNewsBtnClick() }
            switchBtn.setOnCheckedChangeListener { _, isChecked ->
                newsHeadListener?.onCheckedChange(isChecked)
            }
        }
    }

    interface NewsHeadListener {
        fun onNewsBtnClick()
        fun onCheckedChange(isChecked: Boolean)
    }

    interface OnNewsItemClickListener {
        fun onClick(news: News)
    }

    interface CourseListener {
        fun onClick()
    }

    data class HomeCourseData(
        var updating: Boolean = false,
        var loading: Boolean = false,
        var updateError: String? = null,
        var course: HomeCourse? = null
    )
}