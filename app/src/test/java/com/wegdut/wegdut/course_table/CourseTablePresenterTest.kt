package com.wegdut.wegdut.course_table

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.wegdut.wegdut.CoroutinesModelTestUtils.runBlocking
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.course_table.CourseTableData
import com.wegdut.wegdut.data.course_table.CourseTableRepository
import com.wegdut.wegdut.ui.course_table.CourseTableContract
import com.wegdut.wegdut.ui.course_table.CourseTablePresenter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*

class CourseTablePresenterTest {
    @Mock
    lateinit var view: CourseTableContract.View

    @Mock
    lateinit var repository: CourseTableRepository
    private lateinit var order: InOrder
    private val presenter = CourseTablePresenter()

    @Before
    fun setup() {
        MyLog.isTesting = true
        MockitoAnnotations.initMocks(this)
        presenter.repository = repository
        presenter.start()
        order = inOrder(view)
    }

    @After
    fun teardown() {
        presenter.stop()
    }

    @Test
    fun `学期缓存和API都正常时的订阅和测试`() {
        termCache(true)
        termApi(true)
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setTermLoadingError(null)
        order.verify(view).setTermLoading(true)
        order.verify(view).setTerms(anyList())
        order.verify(view).setTermLoading(false)
        order.verify(view).setTerms(anyList())
        order.verify(view).setTermLoading(false)
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `学期缓存异常而API正常时的订阅测试`() {
        termCache(false)
        termApi(true)
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setTermLoadingError(null)
        order.verify(view).setTermLoading(true)
        order.verify(view).setTerms(anyList())
        order.verify(view).setTermLoading(false)
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `学期缓存正常而API异常时的订阅测试`() {
        termCache(true)
        termApi(false)
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setTermLoadingError(null)
        order.verify(view).setTermLoading(true)
        order.verify(view).setTerms(anyList())
        order.verify(view).setTermLoading(false)
        order.verify(view).setTermLoadingError(anyString())
        order.verify(view).setTermLoading(false)
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `课表缓存和API都正常时获取课表测试`() {
        courseCache(true)
        courseApi(true)
        presenter.runBlocking {
            presenter.subscribe(view)
            presenter.getCourseData("")
        }
        order.verify(view).setCourseLoadingError(null)
        order.verify(view).setCourseLoading(true)
        order.verify(view).setCourseData(any())
        order.verify(view).setCourseLoading(false)
        order.verify(view).setCourseData(any())
        order.verify(view).setCourseLoading(false)
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `课表缓存异常而API正常时获取课表测试`() {
        courseCache(false)
        courseApi(true)
        presenter.runBlocking {
            presenter.subscribe(view)
            presenter.getCourseData("")
        }
        order.verify(view).setCourseLoadingError(null)
        order.verify(view).setCourseLoading(true)
        order.verify(view).setCourseData(any())
        order.verify(view).setCourseLoading(false)
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `课表缓存正常而API异常时获取课表测试`() {
        courseCache(true)
        courseApi(false)
        presenter.runBlocking {
            presenter.subscribe(view)
            presenter.getCourseData("")
        }
        order.verify(view).setCourseLoadingError(null)
        order.verify(view).setCourseLoading(true)
        order.verify(view).setCourseData(any())
        order.verify(view).setCourseLoading(false)
        order.verify(view).setCourseLoadingError(any())
        order.verify(view).setCourseLoading(false)
        order.verifyNoMoreInteractions()
    }

    private fun termCache(ok: Boolean) {
        val action = `when`(repository.getTermsFromCache())
        if (ok)
            action.thenReturn(emptyList())
        else
            action.thenThrow(RuntimeException())
    }

    private fun termApi(ok: Boolean) {
        val action = `when`(repository.getTermsFromApi())
        if (ok)
            action.thenReturn(emptyList())
        else
            action.thenThrow(RuntimeException())
    }

    private fun courseCache(ok: Boolean) {
        val action = `when`(repository.getCourseFromCache(any()))
        if (ok)
            action.thenReturn(CourseTableData(Date(), emptyList()))
        else
            action.thenThrow(RuntimeException())
    }

    private fun courseApi(ok: Boolean) {
        val action = `when`(repository.getCourseFromApi(any()))
        if (ok)
            action.thenReturn(CourseTableData(Date(), emptyList()))
        else
            action.thenThrow(RuntimeException())
    }
}