package com.wegdut.wegdut.utils

import android.graphics.Color
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.data.edu.course.CourseStatus
import java.util.*
import kotlin.math.absoluteValue

object CourseUtils {

    private val colorStrings = listOf(
        "#F44336", "#673AB7", "#0288D1", "#2E7D32", "#F57F17", "#E64A19", "#455A64"
    )
    private lateinit var colors: List<Int>

    // 时间表，单位为分钟
    private val startTimes = mapOf(
        1 to 8 * 60 + 30,   //  8:30
        3 to 10 * 60 + 25,  // 10:25
        5 to 13 * 60 + 50,  // 13:50
        6 to 14 * 60 + 40,  // 14:40
        8 to 16 * 60 + 30,  // 16:30
        10 to 18 * 60 + 30, // 18:30
    )

    /**
     * 返回课程时间对应的字符串，例如 08:30 - 10:05
     */
    fun getTime(course: Course): String {
        val t = calTime(course.from, course.to)
        val toStr = { minutes: Int ->
            val h = minutes / 60
            val m = minutes % 60
            "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
        }
        return "${toStr(t.first)} - ${toStr(t.second)}"
    }

    /**
     * 返回课程时间（不包括日期），单位为秒
     */
    fun getTimeInSecond(course: Course): Pair<Int, Int> {
        val t = calTime(course.from, course.to)
        return t.first * 60 to t.second * 60
    }

    /**
     * @param from  开始节次
     * @param to    结束节次，包含
     * 返回课程时间，单位为分钟
     */
    private fun calTime(from: Int, to: Int): Pair<Int, Int> {
        val startTime = startTimes[from] ?: 0
        val totalTime = (to - from) * 50 + 45
        return startTime to startTime + totalTime
    }

    fun status(course: Course): CourseStatus {
        val now = Date().time
        val min2ms = 60 * 1000
        val t = calTime(course.from, course.to)
        val s = course.date.time + t.first * min2ms
        val e = course.date.time + t.second * min2ms
        return when {
            e < now -> CourseStatus.FINISHED
            s > now -> CourseStatus.PENDING
            else -> CourseStatus.RUNNING
        }
    }

    private fun weeksBetween(firstDate: Date, secondDate: Date): Int {
        return DateUtils.relativeWeekOfDate(secondDate) - DateUtils.relativeWeekOfDate(firstDate)
    }

    fun weekAndWeekDay(startDate: Date): String {
        val now = Date()
        val week = weeksBetween(startDate, now) + 1
        val weekDay = "星期${DateUtils.weekDayInChinese(now)}"
        if (week !in 1..22) return weekDay
        return "第${week}周 $weekDay"
    }

    private fun initColors() {
        if (this::colors.isInitialized) return
        colors = colorStrings.map { Color.parseColor(it) }
    }

    fun getColor(name: String): Int {
        initColors()
        val v = name[0].toInt()
        return colors[v.absoluteValue % colors.size]
    }
}