package com.wegdut.wegdut.utils

import android.content.Context
import android.widget.Toast

object MessageUtils {
    fun info(context: Context?, msg: String) {
        context?.let { Toast.makeText(it, msg, Toast.LENGTH_LONG).show() }
    }
}