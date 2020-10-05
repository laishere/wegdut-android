package com.wegdut.wegdut.ui.message

import com.wegdut.wegdut.di.FragmentScoped
import dagger.Binds
import dagger.Module

@Module
interface MessageModule {
    @FragmentScoped
    @Binds
    fun presenter(presenter: MessagePresenter): MessageContract.Presenter
}