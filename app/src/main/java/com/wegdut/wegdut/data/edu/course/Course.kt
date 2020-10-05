package com.wegdut.wegdut.data.edu.course

import java.util.*

data class Course(
    var date: Date,
    val from: Int,
    val to: Int,
    val name: String,
    val teacher: String,
    val location: String,
    val intro: String = ""
)