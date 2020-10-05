package com.wegdut.wegdut.ui.home

import com.wegdut.wegdut.data.home.course.HomeCourse
import com.wegdut.wegdut.data.news.News
import com.wegdut.wegdut.ui.BasePresenter

class HomeContract {
    interface View {
        fun setCourse(updated: Boolean, course: HomeCourse)
        fun setCourseUpdating(updating: Boolean)
        fun setCourseLoading(loading: Boolean)
        fun setCourseRefreshing(refreshing: Boolean)
        fun setCourseUpdateError(error: String?)
        fun setNews(news: List<News>)
        fun setNewsUpdating(updating: Boolean)
        fun setNewsLoading(loading: Boolean)
        fun setNewsRefreshing(refreshing: Boolean)
        fun setNewsUpdateError(error: String?)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun refresh()
        abstract fun refreshCourse()
        abstract fun refreshNews()
        abstract fun setOnlyForStudent(onlyForStudent: Boolean)
    }
}