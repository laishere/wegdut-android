package com.wegdut.wegdut.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.course_table.WeekCourse
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.data.edu.course.CourseStatus
import com.wegdut.wegdut.utils.CourseUtils

class CourseTable(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    companion object {
        private const val GAP_FACTOR = 0.01f
    }

    var onCourseItemClickListener: OnCourseItemClickListener? = null

    var weekCourse: WeekCourse? = null
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            if (value == null) return
            val count = value.sumBy { it.course.size }
            while (childCount < count) {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_course_table_text_view, this, false)
                addView(view)
            }
            while (childCount > count) removeViewAt(childCount - 1)
            var i = 0
            for (dayCourse in value) {
                for (course in dayCourse.course) {
                    val wrapper = getChildAt(i)
                    wrapper.setOnClickListener {
                        onCourseItemClickListener?.onClick(course)
                    }
                    val name = wrapper.findViewById<TextView>(R.id.name)
                    val location = wrapper.findViewById<TextView>(R.id.location)
                    name.text = course.name
                    location.text = course.location
                    val bg = wrapper.background as GradientDrawable
                    bg.mutate()
                    val color =
                        if (CourseUtils.status(course) == CourseStatus.FINISHED)
                            ResourcesCompat.getColor(
                                resources,
                                R.color.course_table_item_inactive,
                                null
                            )
                        else CourseUtils.getColor(course.name)
                    bg.setColor(color)
                    i++
                }
            }
        }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (weekCourse == null) return
        val w = measuredWidth - paddingLeft - paddingRight
        val h = measuredHeight - paddingTop - paddingBottom
        val gap = (w * GAP_FACTOR).toInt()
        var i = 0
        for (dayCourse in weekCourse!!) {
            for (course in dayCourse.course) {
                val ch = getChildAt(i)
                val top = (h - gap) * (course.from - 1) / 12 + gap
                val left = (w - gap) * (dayCourse.weekDay - 1) / 7 + gap
                ch.layout(left, top, left + ch.measuredWidth, top + ch.measuredHeight)
                i++
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (weekCourse == null) return
        val w = measuredWidth - paddingLeft - paddingRight
        val h = measuredHeight - paddingTop - paddingBottom
        val gap = (w * GAP_FACTOR).toInt()
        val chW = (w - gap) / 7
        var i = 0
        for (dayCourse in weekCourse!!) {
            for (course in dayCourse.course) {
                val ch = getChildAt(i)
                val len = course.to - course.from + 1
                val chH = len * (h - gap) / 12
                ch.measure(
                    MeasureSpec.makeMeasureSpec(chW - gap, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(chH - gap, MeasureSpec.EXACTLY)
                )
                i++
            }
        }
    }

    interface OnCourseItemClickListener {
        fun onClick(course: Course)
    }
}
