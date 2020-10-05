package com.wegdut.wegdut.data.edu.course

import java.util.*

data class DayCourse(
    var date: Date = Date(),
    var week: Int = 0,
    var weekDay: Int = 0,
    var label: String = "",
    val course: List<Course> = emptyList(),
    var noCourseDesc: String = ""
)