package com.wegdut.wegdut.data.course_table

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.data.edu.CommonEduRepository
import com.wegdut.wegdut.data.edu.Term
import com.wegdut.wegdut.data.edu.course.CommonCourseRepository
import com.wegdut.wegdut.room.v1.edu.course.CourseDao
import com.wegdut.wegdut.room.v1.edu.course.CourseInfoDao
import javax.inject.Inject

class CourseTableRepositoryImpl @Inject constructor() : CourseTableRepository {
    @Inject
    lateinit var commonCourseRepository: CommonCourseRepository

    @Inject
    lateinit var commonEduRepository: CommonEduRepository

    @Inject
    lateinit var courseDao: CourseDao

    @Inject
    lateinit var courseInfoDao: CourseInfoDao
    private var courseLocalCache: CourseTableData? = null
    private var courseApiCache: CourseTableData? = null
    private var courseLocalTerm: String? = null
    private var courseApiTerm: String? = null
    private var termLocalCache: List<Term>? = null
    private var termApiCache: List<Term>? = null

    override fun getCourseFromCache(term: String): CourseTableData {
        if (courseLocalTerm == term)
            return courseLocalCache!!
        courseLocalCache = getCourseTableFromCache(term)
        courseLocalTerm = term
        return courseLocalCache!!
    }

    override fun getCourseFromApi(term: String): CourseTableData {
        if (courseApiTerm == term)
            return courseApiCache!!
        courseApiCache = getCourseTableFromApi(term)
        courseLocalCache = courseApiCache
        courseApiTerm = term
        courseLocalTerm = term
        return courseApiCache!!
    }

    override fun getTermsFromCache(): List<Term> {
        if (termLocalCache != null) return termLocalCache!!
        termLocalCache = commonEduRepository.getTermsFromCache()
        return termLocalCache!!
    }

    override fun getTermsFromApi(): List<Term> {
        if (termApiCache != null)
            return termApiCache!!
        termApiCache = commonEduRepository.getTermsFromApi()
        termLocalCache = termApiCache
        return termApiCache!!
    }

    override fun refresh() {
        courseLocalCache = null
        courseApiCache = null
        courseApiTerm = null
        courseLocalTerm = null
        termLocalCache = null
        termApiCache = null
    }

    private fun getCourseTableFromApi(term: String): CourseTableData {
        commonCourseRepository.getCourse(term)
        return getCourseTableFromCache(term)
    }

    private fun getCourseTableFromCache(term: String): CourseTableData {
        val info = courseInfoDao.get(term) ?: throw MyException("无法获取课程信息")
        val weekCourses = List(info.totalWeek) {
            val list = courseDao.getByTermAndWeek(term, it + 1)
            list.map { c -> c.toDayCourse() }
        }
        return CourseTableData(info.startDate, weekCourses)
    }
}