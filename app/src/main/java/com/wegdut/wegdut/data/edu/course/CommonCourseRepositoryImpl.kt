package com.wegdut.wegdut.data.edu.course

import com.wegdut.wegdut.api.EduApi
import com.wegdut.wegdut.room.v1.edu.course.*
import com.wegdut.wegdut.room.v1.edu.course.DayCourseEntity.Companion.toEntity
import com.wegdut.wegdut.utils.ApiUtils.extract
import com.wegdut.wegdut.utils.CourseUtils
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
            map[k]!!.add(CourseUtils.toCourse(d, c))
        }
        val courseMap = mutableMapOf<Date, DayCourse>()
        for (i in map) {
            val d = DateUtils.onlyDate(i.value.first().start)
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
        val newCourseMap = mutableMapOf<Date, DayCourse>()
        val startWeek = DateUtils.relativeWeekOfDate(dto.startDate)
        val courseBreaks = mutableListOf<CourseBreakEntity>()
        for (t in dto.transformation) {
            val week = DateUtils.relativeWeekOfDate(t.date) - startWeek + 1
            val weekDay = DateUtils.weekDay(t.date) + 1
            if (t.sourceDate == null) {
                val course = DayCourse(t.date, week, weekDay, t.type, emptyList())
                newCourseMap[t.date] = course
            } else {
                val course = courseMap.remove(t.sourceDate)
                course?.let {
                    it.week = week
                    it.weekDay = weekDay
                    it.date = t.date
                    for (c in it.course) CourseUtils.exchangeCourse(t.date, c)
                    newCourseMap[t.date] = it
                }
                // 源课程可能不存在，直接对调课前的对应日期课程label进行赋值
                courseMap[t.date]?.let { it.label = t.type }
            }
            if ("休" in t.type) {
                val courseBreakEntity =
                    CourseBreakEntity(
                        t.date,
                        term
                    )
                courseBreaks.add(courseBreakEntity)
            }
        }
        for (i in newCourseMap)
            courseMap[i.key] = i.value
        return courseBreaks
    }
}