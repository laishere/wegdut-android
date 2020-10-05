package com.wegdut.wegdut.ui.message

import com.wegdut.wegdut.data.message.MessageUnread
import com.wegdut.wegdut.ui.BasePresenter

class MessageContract {
    interface View {
        fun setMessageUnread(messageUnread: MessageUnread)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun updateUnread()
    }
}