package com.wegdut.wegdut.ui

abstract class BasePresenter<T : Any> : BaseCoroutineModel() {
    protected var view: T? = null
    private var subscribeTimes = 0
    protected val isResubscribe: Boolean
        get() = subscribeTimes > 1

    open fun subscribe(view: T) {
        this.view = view
        subscribeTimes++
    }

    open fun unsubscribe() {
        view = null
    }
}