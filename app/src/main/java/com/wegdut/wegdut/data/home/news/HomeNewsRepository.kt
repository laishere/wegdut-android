package com.wegdut.wegdut.data.home.news

import com.wegdut.wegdut.data.news.News

interface HomeNewsRepository {
    fun getFromCache(onlyForStudent: Boolean): List<News>
    fun getFromApi(onlyForStudent: Boolean): List<News>
    fun refresh()
}