package com.wegdut.wegdut

import android.util.Log

object MyLog {
    var isTesting = false
    fun error(who: Any, e: Throwable, msg: String? = null) {
        if (!isTesting)
            Log.e(who.javaClass.name, msg, e)
    }

    fun debug(who: Any, msg: String) {
        if (BuildConfig.DEBUG && !isTesting)
            Log.d(who.javaClass.name, msg)
    }

}