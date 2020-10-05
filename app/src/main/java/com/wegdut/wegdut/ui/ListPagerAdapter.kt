package com.wegdut.wegdut.ui

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

abstract class ListPagerAdapter<T>(items: List<T>? = null) : PagerAdapter() {
    var items: List<T>? = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int {
        return items?.size ?: 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}