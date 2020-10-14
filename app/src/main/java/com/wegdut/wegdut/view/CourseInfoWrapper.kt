package com.wegdut.wegdut.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.utils.CourseUtils

class CourseInfoWrapper(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val viewHolder: ViewHolder
    private var courses: List<Course>? = null

    init {
        val v = LayoutInflater.from(context).inflate(R.layout.item_course_info, this, false)
        addView(v)
        viewHolder = ViewHolder(v)
    }

    fun setCourses(courses: List<Course>) {
        this.courses = courses
    }

    fun show(pos: Int) {
        val c = courses?.getOrNull(pos) ?: return
        viewHolder.bind(c)
    }

    private class ViewHolder(view: View) {
        private val timeTextView: TextView = view.findViewById(R.id.time)
        private val locationTextView: TextView = view.findViewById(R.id.location)
        private val teacherTextView: TextView = view.findViewById(R.id.teacher)
        private val introWrapper: View = view.findViewById(R.id.intro_wrapper)
        private val introTextView: TextView = view.findViewById(R.id.intro)

        @SuppressLint("SetTextI18n")
        fun bind(course: Course) {
            timeTextView.text = "${CourseUtils.getTime(course)}  ${course.from} - ${course.to}节"
            if (course.location.isNotBlank()) {
                locationTextView.text = course.location
                locationTextView.visibility = View.VISIBLE
            } else locationTextView.visibility = View.GONE
            teacherTextView.text = course.teacher
            if (course.intro.isNotBlank()) {
                introWrapper.visibility = View.VISIBLE
                introTextView.text = course.intro
            } else introWrapper.visibility = View.GONE
        }
    }
}