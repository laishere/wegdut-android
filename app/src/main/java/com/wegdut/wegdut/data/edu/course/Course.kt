package com.wegdut.wegdut.data.edu.course

import java.util.*

data class Course(
    var start: Date,
    var end: Date,
    val from: Int,
    val to: Int,
    val name: String,
    val teacher: String,
    val location: String,
    val intro: String = ""
)