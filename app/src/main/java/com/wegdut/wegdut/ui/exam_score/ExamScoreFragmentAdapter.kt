package com.wegdut.wegdut.ui.exam_score

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.wegdut.wegdut.data.edu.Term

class ExamScoreFragmentAdapter(
    var terms: List<Term>?,
    fm: FragmentManager
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var gradePoints: SparseArray<String>? = null

    override fun getItem(position: Int): Fragment {
        return ExamScoreFragment()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val f = super.instantiateItem(container, position) as ExamScoreFragment
        f.setTerm(terms!![position])
        return f
    }

    override fun getCount(): Int {
        return terms?.size ?: 0
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun getPageTitle(position: Int): CharSequence? {
        val term = terms!![position]
        var termTitle = "${term.yearName}${term.termName}"
        gradePoints?.let {
            if (it[position] != null)
                termTitle += " ${it[position]}"
        }
        return termTitle
    }
}