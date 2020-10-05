package com.wegdut.wegdut.data.edu.course

import java.util.*

data class CourseTableDto(
    val startDate: Date,
    val totalWeek: Int,
    val course: List<CourseDto>,
    val transformation: List<CourseTransformationDto>,
    val holiday: List<HolidayDto>
)