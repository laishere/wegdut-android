package com.wegdut.wegdut.ui.user

import com.wegdut.wegdut.data.user.UserRepository
import com.wegdut.wegdut.data.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface UserModule {
    @Binds
    fun presenter(presenter: UserPresenter): UserContract.Presenter

    @Binds
    fun repository(repository: UserRepositoryImpl): UserRepository
}