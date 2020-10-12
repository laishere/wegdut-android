package com.wegdut.wegdut.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

open class ViewSwitcher(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var current = -1

    fun show(index: Int) {
        current = index
        updateChildrenVisibility()
    }

    private fun updateChildrenVisibility() {
        for (i in 0 until childCount) {
            val c = getChildAt(i)
            c.visibility = if (i == current) VISIBLE else GONE
        }
    }
}