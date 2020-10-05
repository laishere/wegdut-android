package com.wegdut.wegdut

import com.wegdut.wegdut.utils.DateUtils
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateUtilsTest {
    @Test
    fun `去除时间的日期格式转换测试`() {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val test = arrayListOf("2018-09-01 23:00:01.303", "2019-01-01 00:00:01.001")
        val exp = arrayListOf("2018-09-01 00:00:00.000", "2019-01-01 00:00:00.000")
        val result = test.map {
            val d = formatter.parse(it)
            formatter.format(DateUtils.onlyDate(d!!))
        }
        Assert.assertEquals(exp, result)
    }

    @Test
    fun `计算给定时间所在星期的第一天（星期一）测试`() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val test = arrayOf("2019-12-09", "2019-01-01", "2019-12-15")
        val exp = arrayOf("2019-12-09", "2018-12-31", "2019-12-09")
        val result = test.map { DateUtils.mondayOfGivenDate(formatter.parse(it)!!) }
        val expDate = exp.map { formatter.parse(it)!! }
        Assert.assertEquals(expDate, result)
    }

    @Test
    fun `相对星期计算测试`() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val first = DateUtils.relativeWeekOfDate(formatter.parse("2019-09-02")!!)
        val test = arrayListOf("2019-12-09", "2019-09-02", "2019-12-15", "2019-12-16")
        val exp = arrayListOf(first + 14, first, first + 14, first + 15)
        val result = test.map { DateUtils.relativeWeekOfDate(formatter.parse(it)!!) }
        Assert.assertEquals(exp, result)
    }

    @Test
    fun `中文星期计算测试`() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val test = arrayListOf("2019-12-11", "2019-12-15")
        val exp = arrayListOf("三", "日")
        val result = test.map { DateUtils.weekDayInChinese(formatter.parse(it)!!) }
        Assert.assertEquals(exp, result)
    }

    @Test
    fun `时间缩写计算测试`() {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = Date().time
        val second = formatter.format(Date(now - 59 * 1000))
        val min = formatter.format(Date(now - 59 * 60 * 1000))
        val hour = formatter.format(Date(now - 23 * 3600 * 1000))
        val day = formatter.format(Date(Date().time - 72 * 3600 * 1000 + 1000))
        val test = arrayListOf(
            "2028-11-30 11:00:00", "2019-12-31 12:00:00", "2019-12-11 11:30:57",
            "2018-12-18 23:33:23", second, min, hour, day
        )
        val exp = arrayListOf(
            "2028年11月30日",
            "2019年12月31日",
            "2019年12月11日",
            "2018年12月18日",
            "刚刚",
            "59分钟前",
            "23小时前",
            "2天前"
        )
        val result = test.map { DateUtils.format(formatter.parse(it)!!) }
        Assert.assertEquals(exp, result)
    }

    @Test
    fun `仅日期的时间缩写计算测试`() {
        fun func(offsetDay: Int): Date {
            val now = Date().time
            return Date(
                now + TimeUnit.MILLISECONDS.convert(
                    offsetDay.toLong(),
                    TimeUnit.DAYS
                ) + 100 * offsetDay
            )
        }

        val test = arrayListOf(2, 1, 0, -1, -2)
        val result = test.map { DateUtils.format(func(it), DateUtils.FormatType.SHORT_DATE) }
        val exp = arrayListOf("后天", "明天", "今天", "昨天", "前天")
        Assert.assertEquals(exp, result)
    }
}