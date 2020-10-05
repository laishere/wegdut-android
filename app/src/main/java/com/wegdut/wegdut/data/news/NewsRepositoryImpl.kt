package com.wegdut.wegdut.data.news

import com.wegdut.wegdut.api.NewsApi
import com.wegdut.wegdut.data.PageWrapper
import com.wegdut.wegdut.utils.ApiUtils.extract

class NewsRepositoryImpl(private val type: String, private val api: NewsApi) : NewsRepository() {
    override fun get(count: Int, offset: Int, marker: String?): PageWrapper<News> {
        return api.getLatest(type, count, offset, marker).extract().map { it.toNews() }
    }
}