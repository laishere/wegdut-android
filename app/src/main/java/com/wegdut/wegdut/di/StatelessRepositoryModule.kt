package com.wegdut.wegdut.di

import com.wegdut.wegdut.data.edu.CommonEduRepository
import com.wegdut.wegdut.data.edu.CommonEduRepositoryImpl
import com.wegdut.wegdut.data.edu.course.CommonCourseRepository
import com.wegdut.wegdut.data.edu.course.CommonCourseRepositoryImpl
import com.wegdut.wegdut.data.remote_storage.OSSRepository
import com.wegdut.wegdut.data.remote_storage.StorageRepository
import com.wegdut.wegdut.data.student_verification.StudentVerificationRepository
import com.wegdut.wegdut.data.student_verification.StudentVerificationRepositoryImpl
import com.wegdut.wegdut.data.user.AccountInfoRepository
import com.wegdut.wegdut.data.user.AccountInfoRepositoryImpl
import com.wegdut.wegdut.data.user.LoginRepository
import com.wegdut.wegdut.data.user.LoginRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface StatelessRepositoryModule {
    @Singleton
    @Binds
    fun storageRepository(repository: OSSRepository): StorageRepository

    @Singleton
    @Binds
    fun loginRepository(repository: LoginRepositoryImpl): LoginRepository

    @Singleton
    @Binds
    fun accountInfoRepository(repository: AccountInfoRepositoryImpl): AccountInfoRepository

    @Singleton
    @Binds
    fun commonCourseRepository(repository: CommonCourseRepositoryImpl): CommonCourseRepository

    @Singleton
    @Binds
    fun commonEduRepository(repository: CommonEduRepositoryImpl): CommonEduRepository

    @Singleton
    @Binds
    fun studentVerificationRepository(repository: StudentVerificationRepositoryImpl): StudentVerificationRepository
}