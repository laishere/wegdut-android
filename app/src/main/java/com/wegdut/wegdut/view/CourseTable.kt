package com.wegdut.wegdut.view

import android.content.Context
import android.util.AttributeSet
import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.keyIterator
import androidx.core.util.set
import androidx.core.util.valueIterator
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.course_table.WeekCourse
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.data.edu.course.DayCourse
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class CourseTable(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    var onCourseItemClickListener: OnCourseItemClickListener? = null

    private val overlapCourseWrappers = SparseArray<List<OverlapCourseWrapper>>()

    fun setWeekCourse(weekCourse: WeekCourse?) {
        overlapCourseWrappers.clear()
        removeAllViews()
        if (weekCourse == null)
            return
        for (i in weekCourse)
            addDayCourse(i)
        requestLayout()
    }

    private fun addDayCourse(dayCourse: DayCourse) {
        val items = mutableListOf<CoursesItem>()
        for (i in dayCourse.course) {
            var added = false
            for (j in items) {
                if (j.add(i)) {
                    added = true
                    break
                }
            }
            if (!added)
                items.add(CoursesItem(i.from, i.to, mutableListOf(i)))
        }
        overlapCourseWrappers[dayCourse.weekDay] = items.map { item ->
            val v = OverlapCourseWrapper(context).apply {
                setCourses(item.courses)
            }
            addView(v)
            v.setOnClickListener {
                onCourseItemClickListener?.onClick(item.courses)
            }
            v
        }
    }

    private data class CoursesItem(
        var from: Int,
        var to: Int,
        val courses: MutableList<Course> = mutableListOf()
    ) {
        private fun overlap(course: Course): Boolean {
            val dist = ((course.from + course.to + 1) - (from + to + 1)).absoluteValue
            val totalLength = course.to + 1 - course.from + to + 1 - from
            return dist < totalLength
        }

        fun add(course: Course): Boolean {
            if (!overlap(course)) return false
            courses.add(course)
            from = min(from, course.from)
            to = max(to, course.to)
            return true
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val visibleWidth = measuredWidth - paddingLeft - paddingRight
        val visibleHeight = measuredHeight - paddingTop - paddingBottom
        for (weekday in overlapCourseWrappers.keyIterator()) {
            val wrappers = overlapCourseWrappers[weekday]
            val l = paddingLeft + visibleWidth * (weekday - 1) / 7
            for (wrapper in wrappers) {
                if (wrapper.visibility == GONE) continue
                val t = paddingTop + visibleHeight * (wrapper.from - 1) / 12
                wrapper.layout(l, t, l + wrapper.measuredWidth, t + wrapper.measuredHeight)
                MyLog.debug(this, "$l $t")
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val visibleWidth = measuredWidth - paddingLeft - paddingRight
        val visibleHeight = measuredHeight - paddingTop - paddingBottom
        val w = visibleWidth / 7
        val heightFactor = visibleHeight / 12f
        for (wrappers in overlapCourseWrappers.valueIterator()) {
            for (wrapper in wrappers) {
                val h = heightFactor * (wrapper.to + 1 - wrapper.from)
                wrapper.measure(
                    MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(h.roundToInt(), MeasureSpec.EXACTLY)
                )
            }
        }
    }

    interface OnCourseItemClickListener {
        fun onClick(course: List<Course>)
    }
}
