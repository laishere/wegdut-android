package com.wegdut.wegdut.data.edu.course

import com.wegdut.wegdut.api.EduApi
import com.wegdut.wegdut.room.v1.edu.course.*
import com.wegdut.wegdut.room.v1.edu.course.DayCourseEntity.Companion.toEntity
import com.wegdut.wegdut.utils.ApiUtils.extract
import com.wegdut.wegdut.utils.DateUtils
import java.util.*
import javax.inject.Inject

class CommonCourseRepositoryImpl @Inject constructor() : CommonCourseRepository {

    @Inject
    lateinit var eduApi: EduApi

    @Inject
    lateinit var courseDao: CourseDao

    @Inject
    lateinit var courseInfoDao: CourseInfoDao

    @Inject
    lateinit var courseBreakDao: CourseBreakDao

    @Inject
    lateinit var holidayDao: HolidayDao

    override fun getCourse(term: String) {
        val dto = eduApi.getCourse(term).extract()
        val courseMap = toCourseMap(dto)
        val courseBreaks = transformCourses(term, dto, courseMap)

        // 按学期删除旧课表信息并保存新的课表信息
        val courseList = courseMap.values
        courseDao.deleteByTerm(term)
        courseDao.saveAll(courseList.map { it.toEntity(term) })

        // 保存课表概要信息
        val courseInfoEntity =
            CourseInfoEntity(
                term,
                dto.startDate,
                dto.totalWeek
            )
        courseInfoDao.save(courseInfoEntity)

        // 休息
        courseBreakDao.deleteByTerm(term)
        courseBreakDao.saveAll(courseBreaks)

        // 假期
        val holidays = dto.holiday.map {
            HolidayEntity(
                it.start,
                it.end,
                it.holidayDesc,
                term
            )
        }
        holidayDao.deleteByTerm(term)
        holidayDao.saveAll(holidays)
    }

    private fun toCourseMap(dto: CourseTableDto): MutableMap<Date, DayCourse> {
        val startTime = DateUtils.mondayOfGivenDate(dto.startDate).time
        val map = mutableMapOf<Pair<Int, Int>, MutableList<Course>>()
        for (c in dto.course) {
            val t = ((c.week - 1) * 7 + c.weekDay - 1) * 24 * 3600 * 1000L + startTime
            val d = Date(t)
            val k = Pair(c.week, c.weekDay)
            if (k !in map) map[k] = mutableListOf()
            map[k]!!.add(
                Course(d, c.start, c.end, c.name, c.teacher, c.location, c.intro)
            )
        }
        val courseMap = mutableMapOf<Date, DayCourse>()
        for (i in map) {
            val d = i.value[0].date
            i.value.sortBy { it.from }
            courseMap[d] = DayCourse(d, i.key.first, i.key.second, "", i.value)
        }
        return courseMap
    }

    private fun transformCourses(
        term: String,
        dto: CourseTableDto,
        courseMap: MutableMap<Date, DayCourse>
    ): MutableList<CourseBreakEntity> {
        val startWeek = DateUtils.relativeWeekOfDate(dto.startDate)
        val transform = { d: DayCourse?, date: Date, label: String ->
            val week = DateUtils.relativeWeekOfDate(date) - startWeek + 1
            val weekDay = DateUtils.weekDay(date) + 1
            if (d == null) {
                val course = DayCourse(date, week, weekDay, label, emptyList())
                courseMap[date] = course
            } else {
                d.week = week
                d.weekDay = weekDay
                d.label = label
                d.date = date
                for (c in d.course) c.date = date
                courseMap[date] = d
            }
        }
        val courseBreaks = mutableListOf<CourseBreakEntity>()
        val addToBreaks = { type: String, date: Date ->
            if ("休" in type) {
                val courseBreakEntity =
                    CourseBreakEntity(
                        date,
                        term
                    )
                courseBreaks.add(courseBreakEntity)
            }
        }
        for (t in dto.transformation) {
            val a = courseMap[t.dateB]
            val b = courseMap[t.dateA]
            transform(a, t.dateA, t.typeA)
            transform(b, t.dateB, t.typeB)
            addToBreaks(t.typeA, t.dateA)
            addToBreaks(t.typeB, t.dateB)
        }
        return courseBreaks
    }
}