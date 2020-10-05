package com.wegdut.wegdut.data.edu.exam_plan

import java.util.*

data class ExamPlan(
    val term: String,
    val number: String,
    val name: String,
    val subject: String,
    val week: String,
    val weekDay: String,
    val timeRange: String,
    val campus: String,
    val classroom: String,
    val examType: String,
    val planType: String,
    val teacher: String,
    val start: Date,
    val end: Date
)