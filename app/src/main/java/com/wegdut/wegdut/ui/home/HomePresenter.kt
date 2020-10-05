package com.wegdut.wegdut.ui.home

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.home.course.HomeCourseRepository
import com.wegdut.wegdut.data.home.news.HomeNewsRepository
import com.wegdut.wegdut.event.LoginEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class HomePresenter @Inject constructor() : HomeContract.Presenter() {
    @Inject
    lateinit var courseRepository: HomeCourseRepository

    @Inject
    lateinit var newsRepository: HomeNewsRepository
    private var onlyForStudent = false
    private var refreshingCourse = false
    private var refreshingNews = false

    override fun setOnlyForStudent(onlyForStudent: Boolean) {
        this.onlyForStudent = onlyForStudent
    }

    override fun refresh() {
        jobs.clear()
        refreshCourse()
        refreshNews()
    }

    override fun refreshCourse() {
        refreshingCourse = true
        view?.setCourseRefreshing(true)
        courseRepository.refresh()
        getCourseFromApi()
    }

    override fun refreshNews() {
        refreshingNews = true
        view?.setNewsRefreshing(true)
        newsRepository.refresh()
        getNewsFromApi()
    }

    private fun getCourseFromCache() {
        view?.setCourseLoading(true)
        launch {
            tryIt({
                val course = io {
                    courseRepository.getFromCache()
                }
                view?.setCourseLoading(false)
                view?.setCourse(false, course)
            })
            getCourseFromApi()
        }
    }

    private fun getCourseFromApi() {
        view?.setCourseLoading(true)
        view?.setCourseUpdating(true)
        view?.setCourseUpdateError(null)
        launch {
            tryIt({
                val course = io {
                    courseRepository.getFromApi()
                }
                view?.setCourse(true, course)
            }) {
                val err = MyException.handle(it, "更新课表失败")
                view?.setCourseUpdateError(err)
                MyLog.error(this, it)
            }
            view?.setCourseUpdating(false)
            view?.setCourseLoading(false)
            if (refreshingCourse) {
                refreshingCourse = false
                view?.setCourseRefreshing(false)
            }
        }
    }

    private fun getNewsFromCache() {
        view?.setNewsLoading(true)
        launch {
            tryIt({
                val news = io {
                    newsRepository.getFromCache(onlyForStudent)
                }
                view?.setNewsLoading(false)
                view?.setNews(news)
            })
            getNewsFromApi()
        }
    }

    private fun getNewsFromApi() {
        view?.setNewsUpdating(true)
        view?.setNewsUpdateError(null)
        launch {
            tryIt({
                val news = io {
                    newsRepository.getFromApi(onlyForStudent)
                }
                view?.setNews(news)
            }) {
                val err = MyException.handle(it, "更新通知失败")
                view?.setNewsUpdateError(err)
                MyLog.error(this, it)
            }
            view?.setNewsUpdating(false)
            view?.setNewsLoading(false)
            if (refreshingNews) {
                refreshingNews = false
                view?.setNewsRefreshing(false)
            }
        }
    }

    override fun subscribe(view: HomeContract.View) {
        super.subscribe(view)
        if (isResubscribe) {
            getCourseFromApi()
            getNewsFromApi()
        } else {
            getCourseFromCache()
            getNewsFromCache()
        }
    }

    override fun start() {
        super.start()
        EventBus.getDefault().register(this)
    }

    override fun stop() {
        EventBus.getDefault().unregister(this)
        super.stop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        if (event.status == LoginEvent.Status.LOGIN)
            refreshCourse()
    }
}