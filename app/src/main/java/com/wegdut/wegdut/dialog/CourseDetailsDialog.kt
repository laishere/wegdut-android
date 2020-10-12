package com.wegdut.wegdut.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.view.CourseInfoWrapper
import com.wegdut.wegdut.view.TabView

class CourseDetailsDialog(private val context: Context) {
    private var dialog: Dialog? = null
    private lateinit var tabView: TabView
    private lateinit var courseInfoWrapper: CourseInfoWrapper

    private fun createDialog() {
        dialog = Dialog(context, R.style.SimpleDialog).apply {
            setContentView(R.layout.dialog_course_details)
            tabView = findViewById(R.id.tab_view)
            courseInfoWrapper = findViewById(R.id.course_info_wrapper)
        }
        tabView.setOnTabClickListener(object : TabView.OnTabClickListener {
            override fun onClick(v: View, position: Int) {
                courseInfoWrapper.show(position)
            }
        })
    }

    fun show(courses: List<Course>) {
        if (dialog == null) createDialog()
        updateCourses(courses)
        dialog?.show()
    }

    private fun updateCourses(courses: List<Course>) {
        tabView.setTitles(courses.map { it.name })
        tabView.setCurrentItem(0, false)
        courseInfoWrapper.setCourses(courses)
        courseInfoWrapper.show(0)
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}