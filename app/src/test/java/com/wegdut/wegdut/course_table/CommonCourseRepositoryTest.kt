package com.wegdut.wegdut.course_table

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.inOrder
import com.wegdut.wegdut.ApiTestUtils
import com.wegdut.wegdut.api.EduApi
import com.wegdut.wegdut.data.edu.course.*
import com.wegdut.wegdut.room.v1.edu.course.*
import com.wegdut.wegdut.room.v1.edu.course.DayCourseEntity.Companion.toEntity
import com.wegdut.wegdut.utils.CourseUtils
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.text.SimpleDateFormat
import java.util.*

class CommonCourseRepositoryTest {

    @Mock
    lateinit var eduApi: EduApi

    @Mock
    lateinit var courseDao: CourseDao

    @Mock
    lateinit var courseInfoDao: CourseInfoDao

    @Mock
    lateinit var courseBreakDao: CourseBreakDao

    @Mock
    lateinit var holidayDao: HolidayDao

    private val repository = CommonCourseRepositoryImpl()
    private lateinit var order: InOrder

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        order = inOrder(eduApi, courseDao, courseInfoDao, courseBreakDao, holidayDao)
        repository.eduApi = eduApi
        repository.courseDao = courseDao
        repository.courseInfoDao = courseInfoDao
        repository.courseBreakDao = courseBreakDao
        repository.holidayDao = holidayDao
    }


    @Test
    fun `从API获取课程信息测试`() {
        val dateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        val startDate = dateFormat.parse("2020-8-31")!!
        val totalWeek = 18
        val dtoCourses = listOf(
            CourseDto(
                "subject", "teacher", "class",
                1, 1, 1, 2, "location", "intro", 188
            )
        )
        val otherDate = dateFormat.parse("2020-9-15")!!
        val courseTransforms = listOf(
            CourseTransformationDto(startDate, null, "休"),
            CourseTransformationDto(otherDate, startDate, "补")
        )
        val holidays = listOf(
            HolidayDto(
                dateFormat.parse("2020-10-1")!!,
                dateFormat.parse("2020-10-8")!!,
                "中秋国庆假期"
            )
        )
        val dto = CourseTableDto(startDate, totalWeek, dtoCourses, courseTransforms, holidays)
        val call = ApiTestUtils.okWrapper(dto)
        `when`(eduApi.getCourse(any())).thenReturn(call)

        val term = "term"
        repository.getCourse(term)
        order.verify(eduApi).getCourse(term)
        // 清除旧缓存
        order.verify(courseDao).deleteByTerm(term)
        // 保存课程
        val course = CourseUtils.toCourse(otherDate, dtoCourses[0])
        // 第1周星期一的课程经过调课处理之后应当变成2020-9-15号也就是第3周星期二的课而第1周星期一变为休息日
        val dayCourse1 = DayCourse(startDate, 1, 1, "休", emptyList())
        val dayCourse2 = DayCourse(otherDate, 3, 2, "补", listOf(course))
        val courseEntity1 = dayCourse1.toEntity(term)
        val courseEntity2 = dayCourse2.toEntity(term)
        order.verify(courseDao).saveAll(eq(listOf(courseEntity1, courseEntity2)))

        // 保存课表摘要信息
        val courseInfoEntity = CourseInfoEntity(term, startDate, totalWeek)
        order.verify(courseInfoDao).save(eq(courseInfoEntity))

        // 保存调课休息信息
        val courseBreakEntity = CourseBreakEntity(startDate, term)
        order.verify(courseBreakDao).deleteByTerm(term)
        order.verify(courseBreakDao).saveAll(eq(listOf(courseBreakEntity)))

        // 保存放假信息
        val holidayEntity = holidays[0].run {
            HolidayEntity(
                start, end, holidayDesc, term
            )
        }
        order.verify(holidayDao).deleteByTerm(term)
        order.verify(holidayDao).saveAll(eq(listOf(holidayEntity)))

        order.verifyNoMoreInteractions()
    }
}