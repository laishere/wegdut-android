package com.wegdut.wegdut.data.edu.exam_plan

import java.text.SimpleDateFormat
import java.util.*

data class ExamPlanDto(
    val term: String,
    val number: String,
    val name: String,
    val subject: String,
    val date: String,
    val time: String,
    val week: String,
    val weekDay: String,
    val timeRange: String,
    val campus: String,
    val classroom: String,
    val examType: String,
    val planType: String,
    val teacher: String
) {
    fun toExamPlan(): ExamPlan {
        val format = SimpleDateFormat("yyyy-M-d HH:mm", Locale.CHINA)
        val timeArr = time.split("--")
        val startTime = timeArr[0]
        val endTime = timeArr[1]
        val start = format.parse("$date $startTime")
        val end = format.parse("$date $endTime")
        return ExamPlan(
            term, number, name, subject, week, weekDay, timeRange,
            campus, classroom, examType, planType, teacher, start!!, end!!
        )
    }
}