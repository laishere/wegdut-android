package com.wegdut.wegdut.data.home.course

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.data.edu.CommonEduRepository
import com.wegdut.wegdut.data.edu.Term
import com.wegdut.wegdut.data.edu.course.CommonCourseRepository
import com.wegdut.wegdut.data.edu.course.DayCourse
import com.wegdut.wegdut.room.v1.edu.TermDao
import com.wegdut.wegdut.room.v1.edu.course.CourseBreakDao
import com.wegdut.wegdut.room.v1.edu.course.CourseDao
import com.wegdut.wegdut.room.v1.edu.course.CourseInfoDao
import com.wegdut.wegdut.room.v1.edu.course.HolidayDao
import com.wegdut.wegdut.utils.CourseUtils
import com.wegdut.wegdut.utils.DateUtils
import java.util.*
import javax.inject.Inject

class HomeCourseRepositoryImpl @Inject constructor() : HomeCourseRepository {
    private var localCache: HomeCourse? = null
    private var apiCache: HomeCourse? = null

    @Inject
    lateinit var termDao: TermDao

    @Inject
    lateinit var courseDao: CourseDao

    @Inject
    lateinit var courseInfoDao: CourseInfoDao

    @Inject
    lateinit var holidayDao: HolidayDao

    @Inject
    lateinit var courseBreakDao: CourseBreakDao

    @Inject
    lateinit var commonCourseRepository: CommonCourseRepository

    @Inject
    lateinit var commonEduRepository: CommonEduRepository

    override fun getFromCache(): HomeCourse {
        if (localCache != null) return localCache!!
        localCache = getHomeCourseFromCache()
        return localCache!!
    }

    override fun getFromApi(): HomeCourse {
        if (apiCache != null) return apiCache!!
        val terms = commonEduRepository.getTermsFromApi()
        if (terms.isEmpty()) throw MyException("无法获取学期信息")
        val currentTerm = getCurrentTerm(terms)
        currentTerm?.let { commonCourseRepository.getCourse(it.term) }
        apiCache = getHomeCourseFromCache()
        localCache = apiCache
        return apiCache!!
    }

    override fun refresh() {
        localCache = null
        apiCache = null
    }

    private fun getCurrentTerm(terms: List<Term>): Term? {
        val now = System.currentTimeMillis()
        for (i in terms)
            if (now in i.start.time..i.end.time)
                return i
        return null
    }

    private fun getHomeCourseFromCache(): HomeCourse {
        if (termDao.hasTerm() == null) throw MyException("无法获取学期信息")
        val currentTerm = termDao.getCurrentTerm() ?: return onTermEnd()
        val info = courseInfoDao.get(currentTerm.term) ?: throw MyException("数据库无课程信息")
        val now = Date()
        val w1 = DateUtils.relativeWeekOfDate(info.startDate)
        val w2 = DateUtils.relativeWeekOfDate(now)
        val week = w2 - w1 + 1
        if (week > info.totalWeek)
            return onTermEnd()
        val today = DateUtils.onlyDate(now)
        val tomorrow = Date(today.time + 24 * 3600 * 1000L)
        val getCourse = { date: Date ->
            val courseEntity = courseDao.get(date)
            if (courseEntity == null || courseEntity.course.isEmpty())
                DayCourse(noCourseDesc = noCourseDesc(date))
            else
                courseEntity.toDayCourse()
        }
        val weekAndDay = CourseUtils.weekAndWeekDay(info.startDate)
        val todayCourse = getCourse(today)
        val tomorrowCourse = getCourse(tomorrow)
        return HomeCourse(
            weekAndDay,
            isTermCourseEnd = false,
            hasNextTermCourse = false,
            todayCourse = todayCourse,
            tomorrowCourse = tomorrowCourse
        )
    }

    private fun onTermEnd(): HomeCourse {
        val nextTerm = termDao.getNextTerm()
        val hasNext = if (nextTerm != null) checkHasCourse(nextTerm.term) else false
        val day = "星期${DateUtils.weekDayInChinese(Date())}"
        return HomeCourse(day, true, hasNext, DayCourse(), DayCourse())
    }

    private fun checkHasCourse(term: String): Boolean {
        return courseInfoDao.get(term) != null
    }

    private fun noCourseDesc(date: Date): String {
        val holiday = holidayDao.findHoliday(date)
        if (holiday != null) return holiday.holidayDesc
        val courseBreak = courseBreakDao.find(date)
        if (courseBreak != null) return "调课休息"
        val weekDay = DateUtils.weekDay(date) + 1
        if (weekDay in 6..7) return "周末休息"
        return "无课哦"
    }
}