package com.wegdut.wegdut.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import org.sufficientlysecure.htmltextview.HtmlTextView

class NoTouchHtmlTextView(context: Context?, attrs: AttributeSet?) : HtmlTextView(context, attrs) {
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}