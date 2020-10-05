package com.wegdut.wegdut.ui.message.system

import com.wegdut.wegdut.data.message.MessageRepository
import com.wegdut.wegdut.data.message.system.SystemMessage
import com.wegdut.wegdut.data.message.system.SystemMessageRepository
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
@Module(includes = [SystemModule.BindsModule::class])
class SystemModule {

    @Module
    interface BindsModule {
        @FragmentScoped
        @Binds
        fun repository(repository: SystemMessageRepository): MessageRepository<SystemMessage>

        @FragmentScoped
        @Binds
        fun repository2(repository: MessageRepository<SystemMessage>): ScrollRepository<SystemMessage>

        @FragmentScoped
        @Binds
        fun presenter(presenter: SimpleMessagePresenter<SystemMessage>): SimpleMessageContract.Presenter<SystemMessage>

        @FragmentScoped
        @Binds
        fun adapter(adapter: SystemAdapter): ScrollAdapter<SystemMessage>

        @FragmentScoped
        @Binds
        fun view(view: SystemMessageView): SimpleMessageContract.View<SystemMessage>
    }

    @FragmentScoped
    @Provides
    fun presenter2(presenter: SimpleMessageContract.Presenter<SystemMessage>):
            ScrollContract.Presenter<SystemMessage, ScrollView<SystemMessage>> {
        return presenter as ScrollContract.Presenter<SystemMessage, ScrollView<SystemMessage>>
    }
}