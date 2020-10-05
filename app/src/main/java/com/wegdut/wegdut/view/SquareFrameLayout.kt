package com.wegdut.wegdut.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class SquareFrameLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}