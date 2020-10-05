package com.wegdut.wegdut.ui.send_post

import com.wegdut.wegdut.data.send_post.SendPostRepository
import com.wegdut.wegdut.data.send_post.SendPostRepositoryImpl
import com.wegdut.wegdut.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
interface SendPostModule {
    @ActivityScoped
    @Binds
    fun presenter(presenter: SendPostPresenter): SendPostContract.Presenter

    @ActivityScoped
    @Binds
    fun repository(repository: SendPostRepositoryImpl): SendPostRepository
}