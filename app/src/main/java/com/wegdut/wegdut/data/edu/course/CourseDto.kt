package com.wegdut.wegdut.data.edu.course

data class CourseDto(
    val name: String,
    val teacher: String,
    val classes: String,
    val week: Int,
    val weekDay: Int,
    val start: Int,
    val end: Int,
    val location: String,
    val intro: String,
    val studentCount: Int
)