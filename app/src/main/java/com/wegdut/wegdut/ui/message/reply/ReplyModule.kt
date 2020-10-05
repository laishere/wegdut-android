package com.wegdut.wegdut.ui.message.reply

import com.wegdut.wegdut.data.message.reply.ReplyMessage
import com.wegdut.wegdut.data.message.reply.ReplyMessageRepository
import com.wegdut.wegdut.data.message.reply.ReplyMessageRepositoryImpl
import com.wegdut.wegdut.di.FragmentScoped
import com.wegdut.wegdut.scroll.ScrollAdapter
import com.wegdut.wegdut.scroll.ScrollContract
import com.wegdut.wegdut.scroll.ScrollRepository
import com.wegdut.wegdut.scroll.ScrollView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Suppress("UNCHECKED_CAST")
@Module(includes = [ReplyModule.BindsModule::class])
class ReplyModule {

    @Module
    interface BindsModule {
        @FragmentScoped
        @Binds
        fun repository(repository: ReplyMessageRepositoryImpl): ReplyMessageRepository

        @FragmentScoped
        @Binds
        fun repository2(repository: ReplyMessageRepository): ScrollRepository<ReplyMessage>

        @FragmentScoped
        @Binds
        fun presenter(presenter: ReplyMessagePresenter): ReplyMessageContract.Presenter

        @FragmentScoped
        @Binds
        fun adapter(adapter: ReplyAdapter): ScrollAdapter<ReplyMessage>

        @FragmentScoped
        @Binds
        fun view(view: ReplyMessageView): ReplyMessageContract.View
    }

    @FragmentScoped
    @Provides
    fun presenter2(presenter: ReplyMessageContract.Presenter):
            ScrollContract.Presenter<ReplyMessage, ScrollView<ReplyMessage>> {
        return presenter as ScrollContract.Presenter<ReplyMessage, ScrollView<ReplyMessage>>
    }
}