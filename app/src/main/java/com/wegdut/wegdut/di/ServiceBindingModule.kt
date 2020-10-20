package com.wegdut.wegdut.di

import com.wegdut.wegdut.CourseService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ServiceBindingModule {
    @ServiceScoped
    @ContributesAndroidInjector(modules = [EmptyModule::class])
    fun course(): CourseService
}