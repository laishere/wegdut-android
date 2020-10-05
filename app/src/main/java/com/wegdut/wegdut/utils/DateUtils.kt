package com.wegdut.wegdut.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {
    private const val chineseWeekDays = "日一二三四五六"

    enum class FormatType {
        SHORT,
        TO_MINUTE,
        TO_SECOND,
        SHORT_DATE,
        TIME_TO_MINUTE,
        TIME_TO_SECOND
    }

    fun onlyDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        return calendar.time
    }

    // 返回该日期所在星期的星期一
    fun mondayOfGivenDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        // Monday如果为1，那么Sunday为0，始终令Monday为0而Sunday为6
        val weekday = (calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
        calendar.add(Calendar.DAY_OF_YEAR, -weekday)
        return onlyDate(calendar.time)
    }

    fun weekDay(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val d = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY
        return (d + 7) % 7
    }

    // 相对于2019年1月1日的所在星期
    fun relativeWeekOfDate(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.set(2019, 1, 1)
        val firstDate = mondayOfGivenDate(calendar.time)
        val secondDate = mondayOfGivenDate(date)
        val d = secondDate.time - firstDate.time
        return TimeUnit.DAYS.convert(d, TimeUnit.MILLISECONDS).toInt() / 7
    }

    fun weekDayInChinese(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val d = chineseWeekDays[calendar.get(Calendar.DAY_OF_WEEK) - 1]
        return "$d"
    }

    fun format(date: Date, formatType: FormatType = FormatType.SHORT): String {
        return when (formatType) {
            FormatType.SHORT -> formatShort(date)
            FormatType.SHORT_DATE -> formatShortDate(date)
            FormatType.TO_SECOND -> formatSecond(date)
            FormatType.TIME_TO_MINUTE -> timeToMinute(date)
            FormatType.TIME_TO_SECOND -> timeToSecond(date)
            FormatType.TO_MINUTE -> formatMinute(date)
        }
    }

    fun getTimeInSecond(date: Date): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar.get(Calendar.HOUR_OF_DAY) * 3600 +
                calendar.get(Calendar.MINUTE) * 60 +
                calendar.get(Calendar.SECOND)
    }

    fun deltaDay(a: Date, b: Date): Long {
        val a2 = onlyDate(a)
        val b2 = onlyDate(b)
        return (a2.time - b2.time) / (24 * 3600 * 1000L)
    }

    private fun formatShort(date: Date): String {
        val now = Date()
        val delta = now.time - date.time
        val deltaSec = TimeUnit.SECONDS.convert(delta, TimeUnit.MILLISECONDS)
        return when {
            deltaSec < 0 -> format(date, FormatType.SHORT_DATE)
            deltaSec < 60 -> "刚刚"
            deltaSec < 3600 -> "${deltaSec / 60}分钟前"
            deltaSec < 3600 * 24 -> "${deltaSec / 3600}小时前"
            deltaSec < 3600 * 72 -> "${deltaSec / 3600 / 24}天前"
            else -> format(date, FormatType.SHORT_DATE)
        }
    }

    private fun formatShortDate(date: Date): String {
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now
        val y1 = calendar.get(Calendar.YEAR)
        val m1 = calendar.get(Calendar.MONTH)
        val d1 = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.time = date
        val y2 = calendar.get(Calendar.YEAR)
        val m2 = calendar.get(Calendar.MONTH)
        val d2 = calendar.get(Calendar.DAY_OF_MONTH)
        if (y1 == y2 && m1 == m2) {
            val res = when (d2 - d1) {
                -2 -> "前天"
                -1 -> "昨天"
                0 -> "今天"
                1 -> "明天"
                2 -> "后天"
                else -> null
            }
            if (res != null) return res
        }
        val pattern = if (y1 == y2) "M月d日" else "yyyy年M月d日"
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    private fun formatMinute(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return "${format(date, FormatType.SHORT_DATE)} ${simpleDateFormat.format(date)}"
    }

    private fun formatSecond(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return "${format(date, FormatType.SHORT_DATE)} ${simpleDateFormat.format(date)}"
    }

    private fun timeToMinute(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    private fun timeToSecond(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return simpleDateFormat.format(date)
    }
}