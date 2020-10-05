package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.PageWrapper
import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.news.NewsDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {
    @GET("/news/home")
    fun getHomeNews(
        @Query("category") category: String,
        @Query("onlyForStudent") onlyForStudent: Boolean,
        @Query("limit") limit: Int
    ): Call<ResultWrapper<PageWrapper<NewsDto>>>

    @GET("/news/latest")
    fun getLatest(
        @Query("category") category: String,
        @Query("count") count: Int,
        @Query("offset") offset: Int,
        @Query("marker") marker: String?
    ): Call<ResultWrapper<PageWrapper<NewsDto>>>
}