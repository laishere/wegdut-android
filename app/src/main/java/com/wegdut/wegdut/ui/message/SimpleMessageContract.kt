package com.wegdut.wegdut.ui.message

import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.scroll.ScrollPresenter
import com.wegdut.wegdut.scroll.ScrollView
import com.wegdut.wegdut.ui.Reselectable

class SimpleMessageContract {
    abstract class View<T> : ScrollView<T>(), Reselectable {
        abstract val type: MessageType
    }

    abstract class Presenter<T> : ScrollPresenter<T, View<T>>() {
        abstract fun read(item: T)
    }
}