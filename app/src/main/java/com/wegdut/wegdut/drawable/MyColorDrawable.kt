package com.wegdut.wegdut.drawable

import android.graphics.drawable.ColorDrawable

class MyColorDrawable(color: Int, private val width: Int, private val height: Int) :
    ColorDrawable(color) {
    override fun getIntrinsicHeight() = height
    override fun getIntrinsicWidth() = width
}