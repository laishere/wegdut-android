package com.wegdut.wegdut.ui

abstract class BasePresenterOld<T : Any> {
    protected var view: T? = null
    open fun subscribe(view: T) {
        this.view = view
    }

    open fun unsubscribe() {
        this.view = null
    }
}