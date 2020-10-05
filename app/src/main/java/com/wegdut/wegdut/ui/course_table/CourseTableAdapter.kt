package com.wegdut.wegdut.ui.course_table

import android.view.View
import android.view.ViewGroup
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.course_table.WeekCourse
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.view.CourseTable

class CourseTableAdapter : ListRVAdapter<WeekCourse, CourseTableAdapter.ViewHolder>() {

    var onCourseItemClickListener: CourseTable.OnCourseItemClickListener? = null

    class ViewHolder(itemView: View) : ListRVAdapter.ViewHolder<WeekCourse>(itemView) {
        private val courseTable: CourseTable = itemView as CourseTable
        var onCourseItemClickListener: CourseTable.OnCourseItemClickListener? = null
        override fun bind(data: WeekCourse) {
            courseTable.weekCourse = data
            courseTable.onCourseItemClickListener = onCourseItemClickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflate(parent, R.layout.item_course_table)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onCourseItemClickListener = onCourseItemClickListener
        super.onBindViewHolder(holder, position)
    }
}