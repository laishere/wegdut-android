package com.wegdut.wegdut.ui.news

import com.wegdut.wegdut.api.NewsApi
import com.wegdut.wegdut.data.news.News
import com.wegdut.wegdut.data.news.NewsRepositoryImpl
import com.wegdut.wegdut.di.FragmentScoped
import com.wegdut.wegdut.scroll.*
import dagger.Binds
import dagger.Module
import dagger.Provides

@Suppress("UNCHECKED_CAST")
@Module(includes = [NewsFragmentModule.BindsModule::class])
abstract class NewsFragmentModule(private val type: String) {

    @Module
    interface BindsModule {
        @FragmentScoped
        @Binds
        fun adapter(adapter: NewsAdapter): ScrollAdapter<News>

        @FragmentScoped
        @Binds
        fun presenter(presenter: ScrollPresenter<News, ScrollView<News>>):
                ScrollContract.Presenter<News, ScrollView<News>>
    }

    @FragmentScoped
    @Provides
    fun repository(api: NewsApi): ScrollRepository<News> {
        return NewsRepositoryImpl(type, api)
    }
}