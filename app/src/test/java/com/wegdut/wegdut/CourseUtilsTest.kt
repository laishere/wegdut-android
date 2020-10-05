package com.wegdut.wegdut

import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.utils.CourseUtils
import org.junit.Assert
import org.junit.Test
import java.util.*

class CourseUtilsTest {
    @Test
    fun `课程节次对应时间计算测试`() {
        val testCases = listOf(
            1 to 2,
            3 to 4,
            5 to 7,
            6 to 7,
            8 to 9,
            10 to 12
        )
        val expected = listOf(
            "08:30 - 10:05",
            "10:25 - 12:00",
            "13:50 - 16:15",
            "14:40 - 16:15",
            "16:30 - 18:05",
            "18:30 - 20:55"
        )
        val actual = testCases.map {
            CourseUtils.getTime(
                Course(
                    Date(),
                    it.first,
                    it.second,
                    "",
                    "",
                    "",
                    ""
                )
            )
        }
        Assert.assertEquals(expected, actual)
    }
}