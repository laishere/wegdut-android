package com.wegdut.wegdut.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.utils.CourseUtils

class CourseInfoWrapper(context: Context, attrs: AttributeSet?) : ViewSwitcher(context, attrs) {

    private val viewHolders = mutableListOf<ViewHolder>()

    fun setCourses(courses: List<Course>) {
        if (childCount < courses.size) {
            val layoutInflater = LayoutInflater.from(context)
            for (i in childCount until courses.size) {
                val v = layoutInflater.inflate(R.layout.item_course_info, this, false)
                viewHolders.add(ViewHolder(v))
                addView(v)
            }
        } else {
            for (i in childCount - 1 downTo courses.size) {
                removeViewAt(i)
                viewHolders.removeLast()
            }
        }
        for (i in courses.zip(viewHolders))
            i.second.bind(i.first)
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