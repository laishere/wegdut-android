package com.wegdut.wegdut.ui.message.like

import com.wegdut.wegdut.data.message.MessageRepository
import com.wegdut.wegdut.data.message.like.LikeMessage
import com.wegdut.wegdut.data.message.like.LikeMessageRepository
import com.wegdut.wegdut.di.FragmentScoped
import com.wegdut.wegdut.scroll.ScrollAdapter
import com.wegdut.wegdut.scroll.ScrollContract
import com.wegdut.wegdut.scroll.ScrollRepository
import com.wegdut.wegdut.scroll.ScrollView
import com.wegdut.wegdut.ui.message.SimpleMessageContract
import com.wegdut.wegdut.ui.message.SimpleMessagePresenter
import dagger.Binds
import dagger.Module
import dagger.Provides

@Suppress("UNCHECKED_CAST")
@Module(includes = [LikeModule.BindsModule::class])
class LikeModule {

    @Module
    interface BindsModule {
        @FragmentScoped
        @Binds
        fun repository(repository: LikeMessageRepository): MessageRepository<LikeMessage>

        @FragmentScoped
        @Binds
        fun repository2(repository: MessageRepository<LikeMessage>): ScrollRepository<LikeMessage>

        @FragmentScoped
        @Binds
        fun presenter(presenter: SimpleMessagePresenter<LikeMessage>): SimpleMessageContract.Presenter<LikeMessage>

        @FragmentScoped
        @Binds
        fun adapter(adapter: LikeAdapter): ScrollAdapter<LikeMessage>

        @FragmentScoped
        @Binds
        fun view(view: LikeMessageView): SimpleMessageContract.View<LikeMessage>
    }

    @FragmentScoped
    @Provides
    fun presenter2(presenter: SimpleMessageContract.Presenter<LikeMessage>):
            ScrollContract.Presenter<LikeMessage, ScrollView<LikeMessage>> {
        return presenter as ScrollContract.Presenter<LikeMessage, ScrollView<LikeMessage>>
    }
}