package com.wegdut.wegdut.ui.home

import com.wegdut.wegdut.data.home.course.HomeCourseRepository
import com.wegdut.wegdut.data.home.course.HomeCourseRepositoryImpl
import com.wegdut.wegdut.data.home.news.HomeNewsRepository
import com.wegdut.wegdut.data.home.news.HomeNewsRepositoryImpl
import com.wegdut.wegdut.di.FragmentScoped
import dagger.Binds
import dagger.Module

@Module(includes = [HomeModule.BindsModule::class])
class HomeModule {

    @Module
    interface BindsModule {
        @FragmentScoped
        @Binds
        fun presenter(presenter: HomePresenter): HomeContract.Presenter

        @FragmentScoped
        @Binds
        fun courseRepository(repository: HomeCourseRepositoryImpl): HomeCourseRepository

        @FragmentScoped
        @Binds
        fun newsRepository(repository: HomeNewsRepositoryImpl): HomeNewsRepository
    }
}