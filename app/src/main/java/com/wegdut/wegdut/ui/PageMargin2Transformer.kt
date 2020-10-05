package com.wegdut.wegdut.ui

import android.view.View
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL

class PageMargin2Transformer(private val margin: Int, private val marginBetween: Int) :
    ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        val viewPager = page.parent.parent as ViewPager2
        val offset = position * -(2 * margin - marginBetween)
        if (viewPager.orientation == ORIENTATION_HORIZONTAL) {
            if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                page.translationX = -offset
            } else {
                page.translationX = offset
            }
        } else {
            page.translationY = offset
        }
    }
}