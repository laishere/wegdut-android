package com.wegdut.wegdut.di

import com.wegdut.wegdut.api.*
import com.wegdut.wegdut.utils.ApiUtils
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApiModule {
    @Singleton
    @Provides
    fun accountInfoApi() = ApiUtils.create(AccountInfoApi::class.java)

    @Singleton
    @Provides
    fun blogApi() = ApiUtils.create(PostApi::class.java)

    @Singleton
    @Provides
    fun commentApi() = ApiUtils.create(CommentApi::class.java)

    @Singleton
    @Provides
    fun commentDetailsApi() = ApiUtils.create(CommentDetailsApi::class.java)

    @Singleton
    @Provides
    fun eduApi() = ApiUtils.create(EduApi::class.java)

    @Singleton
    @Provides
    fun likeApi() = ApiUtils.create(LikeApi::class.java)

    @Singleton
    @Provides
    fun loginApi() = ApiUtils.create(LoginApi::class.java)

    @Singleton
    @Provides
    fun messageApi() = ApiUtils.create(MessageApi::class.java)

    @Singleton
    @Provides
    fun newsApi() = ApiUtils.create(NewsApi::class.java)

    @Singleton
    @Provides
    fun registerApi() = ApiUtils.create(RegisterApi::class.java)

    @Singleton
    @Provides
    fun stsApi() = ApiUtils.create(STSApi::class.java)

    @Singleton
    @Provides
    fun studentVerificationApi() = ApiUtils.create(StudentVerificationApi::class.java)

    @Singleton
    @Provides
    fun updateApi() = ApiUtils.create(UpdateApi::class.java)
}