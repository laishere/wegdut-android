package com.wegdut.wegdut.ui.course_table

import com.wegdut.wegdut.data.course_table.CourseTableRepository
import com.wegdut.wegdut.data.course_table.CourseTableRepositoryImpl
import com.wegdut.wegdut.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
interface CourseTableModule {
    @ActivityScoped
    @Binds
    fun repository(repository: CourseTableRepositoryImpl): CourseTableRepository

    @ActivityScoped
    @Binds
    fun presenter(presenter: CourseTablePresenter): CourseTableContract.Presenter
}