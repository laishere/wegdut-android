package com.wegdut.wegdut.ui.post

import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.data.post.PostRepository
import com.wegdut.wegdut.data.post.PostRepositoryImpl
import com.wegdut.wegdut.di.FragmentScoped
import com.wegdut.wegdut.scrollx.ScrollXAdapter
import com.wegdut.wegdut.scrollx.ScrollXContract
import com.wegdut.wegdut.scrollx.ScrollXRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Suppress("UNCHECKED_CAST")
@Module(includes = [PostModule.BindsModule::class])
class PostModule {

    @FragmentScoped
    @Provides
    fun presenter(presenter: PostContract.Presenter): ScrollXContract.Presenter<Post, ScrollXContract.View<Post>> {
        return presenter as ScrollXContract.Presenter<Post, ScrollXContract.View<Post>>
    }

    @Module
    interface BindsModule {
        @FragmentScoped
        @Binds
        fun adapter(adapter: PostAdapter): ScrollXAdapter<Post>

        @FragmentScoped
        @Binds
        fun presenter(presenter: PostPresenter): PostContract.Presenter

        @FragmentScoped
        @Binds
        fun repository(repository: PostRepositoryImpl): PostRepository

        @FragmentScoped
        @Binds
        fun repository2(repository: PostRepository): ScrollXRepository<Post>

        @FragmentScoped
        @Binds
        fun view(view: PostView): PostContract.View
    }
}