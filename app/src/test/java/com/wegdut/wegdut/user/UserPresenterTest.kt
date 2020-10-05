package com.wegdut.wegdut.user

import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.PresenterTestUtils.runBlocking
import com.wegdut.wegdut.data.edu.exam_plan.ExamPlan
import com.wegdut.wegdut.data.user.UserRepository
import com.wegdut.wegdut.ui.user.UserContract
import com.wegdut.wegdut.ui.user.UserPresenter
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class UserPresenterTest {
    @Mock
    lateinit var view: UserContract.View

    @Mock
    lateinit var repository: UserRepository
    private val plans = emptyList<ExamPlan>()
    private val presenter = UserPresenter()
    private lateinit var order: InOrder

    @Before
    fun setup() {
        MyLog.isTesting = true
        MockitoAnnotations.initMocks(this)
        order = inOrder(view)
        presenter.userRepository = repository
        presenter.start()
    }

    @Test
    fun `缓存和api都正常的情况下订阅和刷新测试`() {
        cacheOK()
        apiOK()

        // 第一次订阅
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setExamPlanError(null)
        order.verify(view, times(2)).setExamPlan(plans)

        // 再次订阅
        presenter.runBlocking {
            presenter.unsubscribe()
            presenter.subscribe(view)
        }
        order.verify(view).setExamPlanError(null)
        order.verify(view, times(2)).setExamPlan(plans)

        // 刷新
        presenter.runBlocking {
            presenter.refresh()
        }
        order.verify(view).setRefreshing(true)
        order.verify(view).setExamPlanError(null)
        order.verify(view, times(2)).setExamPlan(plans)
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `缓存正常， api异常下的订阅测试`() {
        cacheOK()
        apiError()
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setExamPlanError(null)
        order.verify(view).setExamPlan(plans)
        order.verify(view).setExamPlanError(anyString())
        order.verifyNoMoreInteractions()
    }

    private fun cacheOK() {
        `when`(repository.getExamPlanFromCache())
            .thenReturn(plans)
    }

    private fun apiOK() {
        `when`(repository.getExamPlanFromApi())
            .thenReturn(plans)
    }

    private fun apiError() {
        `when`(repository.getExamPlanFromApi())
            .thenThrow(RuntimeException())
    }
}