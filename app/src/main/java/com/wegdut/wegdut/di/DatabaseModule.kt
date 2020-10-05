package com.wegdut.wegdut.di

import com.wegdut.wegdut.room.v1.AppDatabaseV1
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun courseDao(db: AppDatabaseV1) = db.courseDao()

    @Singleton
    @Provides
    fun kvDao(db: AppDatabaseV1) = db.kvDao()

    @Singleton
    @Provides
    fun courseBreakDao(db: AppDatabaseV1) = db.courseBreakDao()

    @Singleton
    @Provides
    fun holidayDao(db: AppDatabaseV1) = db.holidayDao()

    @Singleton
    @Provides
    fun newsDao(db: AppDatabaseV1) = db.newsDao()

    @Singleton
    @Provides
    fun courseInfoDao(db: AppDatabaseV1) = db.courseInfoDao()

    @Singleton
    @Provides
    fun termDao(db: AppDatabaseV1) = db.termDao()

    @Singleton
    @Provides
    fun examScoreDao(db: AppDatabaseV1) = db.examScoreDao()
}