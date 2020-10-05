package com.wegdut.wegdut.course_table

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.times
import com.wegdut.wegdut.data.course_table.CourseTableRepositoryImpl
import com.wegdut.wegdut.data.edu.CommonEduRepository
import com.wegdut.wegdut.data.edu.course.CommonCourseRepository
import com.wegdut.wegdut.room.v1.edu.course.CourseDao
import com.wegdut.wegdut.room.v1.edu.course.CourseInfoDao
import com.wegdut.wegdut.room.v1.edu.course.CourseInfoEntity
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.*

class CourseTableRepositoryTest {
    @Mock
    lateinit var commonCourseRepository: CommonCourseRepository

    @Mock
    lateinit var commonEduRepository: CommonEduRepository

    @Mock
    lateinit var courseInfoDao: CourseInfoDao

    @Mock
    lateinit var courseDao: CourseDao

    private val repository = CourseTableRepositoryImpl()
    private val totalWeek = 3
    private lateinit var order: InOrder

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        repository.commonCourseRepository = commonCourseRepository
        repository.commonEduRepository = commonEduRepository
        repository.courseDao = courseDao
        repository.courseInfoDao = courseInfoDao
        order = inOrder(commonCourseRepository, commonEduRepository, courseInfoDao, courseDao)
    }

    @Test
    fun `获取课程本地缓存测试`() {
        val term1 = "term1"
        val term2 = "term2"
        courseCache(true)

        // 初次获取
        repository.getCourseFromCache(term1)
        order.verify(courseInfoDao).get(term1)
        order.verify(courseDao, times(totalWeek)).getByTermAndWeek(eq(term1), any())
        order.verifyNoMoreInteractions()

        // 重复获取时，缓存应该起作用
        repository.getCourseFromCache(term1)
        order.verifyNoMoreInteractions()

        // 获取不同学期课程时，缓存应该过期了
        repository.getCourseFromCache(term2)
        order.verify(courseInfoDao).get(term2)
        order.verify(courseDao, times(totalWeek)).getByTermAndWeek(eq(term2), any())
        order.verifyNoMoreInteractions()

        // 刷新后，缓存应当过期了
        repository.refresh()
        repository.getCourseFromCache(term2)
        order.verify(courseInfoDao).get(term2)
        order.verify(courseDao, times(totalWeek)).getByTermAndWeek(eq(term2), any())
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `获取课程API数据测试`() {
        val term1 = "term1"
        val term2 = "term2"
        courseCache(true)

        // 初次获取。先通过CommonCourseRepository保存课程信息，再从本地数据库中查询
        repository.getCourseFromApi(term1)
        order.verify(commonCourseRepository).getCourse(term1)
        order.verify(courseInfoDao).get(term1)
        order.verify(courseDao, times(totalWeek)).getByTermAndWeek(eq(term1), any())
        order.verifyNoMoreInteractions()

        // 再次获取时，缓存应当起作用
        repository.getCourseFromApi(term1)
        order.verifyNoMoreInteractions()

        // 获取不同学期课程时，缓存应当过期了
        repository.getCourseFromApi(term2)
        order.verify(commonCourseRepository).getCourse(term2)
        order.verify(courseInfoDao).get(term2)
        order.verify(courseDao, times(totalWeek)).getByTermAndWeek(eq(term2), any())
        order.verifyNoMoreInteractions()

        // 刷新后，缓存应当过期了
        repository.refresh()
        repository.getCourseFromApi(term2)
        order.verify(commonCourseRepository).getCourse(term2)
        order.verify(courseInfoDao).get(term2)
        order.verify(courseDao, times(totalWeek)).getByTermAndWeek(eq(term2), any())
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `获取学期信息测试`() {
        termsCache(true)
        termsApi(true)

        // 从本地缓存中获取
        repository.getTermsFromCache()
        order.verify(commonEduRepository).getTermsFromCache()
        order.verifyNoMoreInteractions()

        // 再次获取时，缓存应当起作用
        repository.getTermsFromCache()
        order.verifyNoMoreInteractions()

        // 刷新后，缓存应当过期了
        repository.refresh()
        repository.getTermsFromCache()
        order.verify(commonEduRepository).getTermsFromCache()
        order.verifyNoMoreInteractions()

        // 从api中获取
        repository.getTermsFromApi()
        order.verify(commonEduRepository).getTermsFromApi()
        order.verifyNoMoreInteractions()

        // 再次获取时，缓存应当起作用
        repository.getTermsFromApi()
        order.verifyNoMoreInteractions()

        // 刷新后，缓存应当过期了
        repository.refresh()
        repository.getTermsFromApi()
        order.verify(commonEduRepository).getTermsFromApi()
        order.verifyNoMoreInteractions()

    }

    private fun courseCache(ok: Boolean) {
        val action = `when`(courseInfoDao.get(any()))
        if (ok)
            action.thenReturn(CourseInfoEntity("", Date(), totalWeek))
        else
            action.thenThrow(RuntimeException())
        `when`(courseDao.getByTermAndWeek(any(), any())).thenReturn(emptyList())
    }

    private fun termsCache(ok: Boolean) {
        val action = `when`(commonEduRepository.getTermsFromCache())
        if (ok)
            action.thenReturn(emptyList())
        else
            action.thenThrow(RuntimeException())
    }

    private fun termsApi(ok: Boolean) {
        val action = `when`(commonEduRepository.getTermsFromApi())
        if (ok)
            action.thenReturn(emptyList())
        else
            action.thenThrow(RuntimeException())
    }
}