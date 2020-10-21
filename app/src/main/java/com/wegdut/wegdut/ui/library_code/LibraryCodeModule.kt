package com.wegdut.wegdut.ui.library_code

import com.wegdut.wegdut.data.library_code.LibraryCodeRepository
import com.wegdut.wegdut.data.library_code.LibraryCodeRepositoryImpl
import com.wegdut.wegdut.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
interface LibraryCodeModule {
    @ActivityScoped
    @Binds
    fun presenter(presenter: LibraryCodePresenter): LibraryCodeContract.Presenter

    @ActivityScoped
    @Binds
    fun repository(repository: LibraryCodeRepositoryImpl): LibraryCodeRepository
}