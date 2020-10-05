package com.wegdut.wegdut.data.home.news

import com.wegdut.wegdut.api.NewsApi
import com.wegdut.wegdut.config.Config
import com.wegdut.wegdut.data.news.News
import com.wegdut.wegdut.room.v1.news.NewsDao
import com.wegdut.wegdut.room.v1.news.NewsEntity.Companion.toEntity
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class HomeNewsRepositoryImpl @Inject constructor() : HomeNewsRepository {
    @Inject
    lateinit var newsDao: NewsDao

    @Inject
    lateinit var newsApi: NewsApi
    private var localCache: List<News>? = null
    private var apiCache: List<News>? = null
    private var localOnlyForStudent: Boolean? = null
    private var apiOnlyForStudent: Boolean? = null

    override fun getFromCache(onlyForStudent: Boolean): List<News> {
        if (localCache != null && localOnlyForStudent == onlyForStudent)
            return localCache!!
        localCache = null
        localOnlyForStudent = onlyForStudent
        localCache =
            if (onlyForStudent)
                newsDao.getHomeNotificationOnlyForStudent(Config.homeNewsLimit)
                    .map { it.toNews() }
            else newsDao.getHomeNotification(Config.homeNewsLimit).map { it.toNews() }
        return localCache!!
    }

    override fun getFromApi(onlyForStudent: Boolean): List<News> {
        if (apiCache != null && apiOnlyForStudent == onlyForStudent) return apiCache!!
        apiCache = null
        apiOnlyForStudent = onlyForStudent
        val dto =
            newsApi.getHomeNews(HOME_NEWS_CATEGORY, onlyForStudent, Config.homeNewsLimit).extract()
        val news = dto.list.map { it.toNews() }
        newsDao.deleteAll()
        newsDao.saveAll(news.map { it.toEntity() })
        apiCache = news
        return apiCache!!
    }

    override fun refresh() {
        localCache = null
        apiCache = null
        localOnlyForStudent = null
        apiOnlyForStudent = null
    }

    companion object {
        const val HOME_NEWS_CATEGORY = "校内通知"
    }
}