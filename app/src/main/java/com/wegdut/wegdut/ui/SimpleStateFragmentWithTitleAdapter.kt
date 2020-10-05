package com.wegdut.wegdut.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class SimpleStateFragmentWithTitleAdapter(
    fm: FragmentManager, fragments: Array<Fragment>,
    private val titles: Array<String>
) : SimpleStateFragmentAdapter(fm, fragments) {
    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}