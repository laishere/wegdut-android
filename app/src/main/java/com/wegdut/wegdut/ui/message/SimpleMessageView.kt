package com.wegdut.wegdut.ui.message

import javax.inject.Inject

abstract class SimpleMessageView<T> : SimpleMessageContract.View<T>() {
    @Inject
    lateinit var messagePresenter: SimpleMessageContract.Presenter<T>
    override fun reselect() {
        recyclerView.scrollToPosition(0)
    }
}