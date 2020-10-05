package com.wegdut.wegdut.ui

import android.view.View
import android.widget.TextView

object ViewHolderHelper {
    fun update(v: TextView, text: CharSequence) {
        if (v.text == text) return
        v.text = text
    }

    fun <T : View, D> update(v: T, data: D, autoSave: Boolean = true, update: (T, D) -> Unit) {
        if (getState(v) == data) return
        update.invoke(v, data)
        if (autoSave) setState(v, data)
    }

    fun <T : View, D> save(v: T, data: D) = setState(v, data)

    private fun getState(view: View) = view.tag
    private fun setState(view: View, state: Any?) {
        view.tag = state
    }
}