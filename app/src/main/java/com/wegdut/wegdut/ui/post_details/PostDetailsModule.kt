package com.wegdut.wegdut.ui.post_details

import com.wegdut.wegdut.data.comment.ComplexComment
import com.wegdut.wegdut.data.embedded_comment.EmbeddedCommentRepository
import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.data.post_details.PostDetailsRepositoryImpl
import com.wegdut.wegdut.di.ActivityScoped
import com.wegdut.wegdut.scrollx.ScrollXAdapter
import com.wegdut.wegdut.scrollx.ScrollXContract
import com.wegdut.wegdut.scrollx.ScrollXRepository
import com.wegdut.wegdut.ui.embedded_comment.EmbeddedCommentAdapter
import com.wegdut.wegdut.ui.embedded_comment.EmbeddedCommentContract
import dagger.Binds
import dagger.Module
import dagger.Provides

@Suppress("UNCHECKED_CAST")
@Module(includes = [PostDetailsModule.BindsModule::class])
class PostDetailsModule {
    @Module
    interface BindsModule {
        @ActivityScoped
        @Binds
        fun repository(repository: PostDetailsRepositoryImpl): EmbeddedCommentRepository<Post>

        @ActivityScoped
        @Binds
        fun repository2(repository: EmbeddedCommentRepository<Post>): ScrollXRepository<ComplexComment>

        @ActivityScoped
        @Binds
        fun adapter(adapter: PostDetailsAdapter): EmbeddedCommentAdapter<Post>

        @ActivityScoped
        @Binds
        fun adapter2(adapter: EmbeddedCommentAdapter<Post>): ScrollXAdapter<ComplexComment>

        @ActivityScoped
        @Binds
        fun presenter(presenter: PostDetailsPresenter): EmbeddedCommentContract.Presenter<Post>
    }

    @ActivityScoped
    @Provides
    fun presenter2(presenter: EmbeddedCommentContract.Presenter<Post>):
            ScrollXContract.Presenter<ComplexComment, ScrollXContract.View<ComplexComment>> {
        return presenter as ScrollXContract.Presenter<ComplexComment, ScrollXContract.View<ComplexComment>>
    }

}