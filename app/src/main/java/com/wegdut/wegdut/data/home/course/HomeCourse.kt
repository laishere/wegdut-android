package com.wegdut.wegdut.data.home.course

import com.wegdut.wegdut.data.edu.course.DayCourse

data class HomeCourse(
    val weekAndDay: String,
    val isTermCourseEnd: Boolean,
    val hasNextTermCourse: Boolean,
    val todayCourse: DayCourse,
    val tomorrowCourse: DayCourse
)