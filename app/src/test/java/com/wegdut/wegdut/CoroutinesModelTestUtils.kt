package com.wegdut.wegdut

import com.wegdut.wegdut.ui.BaseCoroutineModel

object CoroutinesModelTestUtils {
    fun BaseCoroutineModel.runBlocking(block: () -> Unit) {
        kotlinx.coroutines.runBlocking {
            scope = this
            block()
        }
    }
}