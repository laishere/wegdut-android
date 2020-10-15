package com.wegdut.wegdut.di

import com.wegdut.wegdut.room.v2.AppDatabaseV2
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun courseDao(db: AppDatabaseV2) = db.courseDao()

    @Singleton
    @Provides
    fun kvDao(db: AppDatabaseV2) = db.kvDao()

    @Singleton
    @Provides
    fun courseBreakDao(db: AppDatabaseV2) = db.courseBreakDao()

    @Singleton
    @Provides
    fun holidayDao(db: AppDatabaseV2) = db.holidayDao()

    @Singleton
    @Provides
    fun newsDao(db: AppDatabaseV2) = db.newsDao()

    @Singleton
    @Provides
    fun courseInfoDao(db: AppDatabaseV2) = db.courseInfoDao()

    @Singleton
    @Provides
    fun termDao(db: AppDatabaseV2) = db.termDao()

    @Singleton
    @Provides
    fun examScoreDao(db: AppDatabaseV2) = db.examScoreDao()
}