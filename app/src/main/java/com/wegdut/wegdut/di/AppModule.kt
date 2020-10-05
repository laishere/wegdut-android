package com.wegdut.wegdut.di

import android.app.Application
import android.content.Context
import com.wegdut.wegdut.room.v1.AppDatabaseV1
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [AppModule.BindModule::class])
class AppModule {

    @Singleton
    @Provides
    fun appDatabase(application: Application) = AppDatabaseV1.getInstance(application)

    @Module
    interface BindModule {
        @Binds
        fun bindContext(application: Application): Context
    }
}