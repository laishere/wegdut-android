package com.wegdut.wegdut

import com.wegdut.wegdut.ui.BasePresenter

object PresenterTestUtils {
    fun BasePresenter<*>.runBlocking(block: () -> Unit) {
        kotlinx.coroutines.runBlocking {
            scope = this
            block()
        }
    }
}