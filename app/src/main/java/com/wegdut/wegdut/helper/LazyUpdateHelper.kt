package com.wegdut.wegdut.helper

import android.os.Handler

class LazyUpdateHelper(var action: () -> Unit) {
    var delay = 100L
    private var isUpdating = false
    fun update() {
        if (isUpdating) return
        isUpdating = true
        Handler().postDelayed({
            action()
            isUpdating = false
        }, delay)
    }
}