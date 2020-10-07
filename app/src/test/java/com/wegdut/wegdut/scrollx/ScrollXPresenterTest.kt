package com.wegdut.wegdut.scrollx

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.never
import com.wegdut.wegdut.CoroutinesModelTestUtils.runBlocking
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.LoadStatus
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.exceptions.verification.VerificationInOrderFailure

class ScrollXPresenterTest {

    @Mock
    lateinit var view: ScrollXContract.View<Int>

    @Mock
    lateinit var repository: ScrollXRepository<Int>

    private val presenter = ScrollXPresenter<Int, ScrollXContract.View<Int>>()
    private lateinit var order: InOrder

    @Before
    fun setup() {
        MyLog.isTesting = true
        MockitoAnnotations.initMocks(this)
        order = inOrder(view, repository)
        presenter.scrollXRepository = repository
        presenter.start()
    }

    @Test
    fun `订阅测试`() {
        `when`(repository.get()).thenReturn(listOf(1))
        `when`(repository.update(any(), any())).thenReturn(ScrollXUpdateAction.LOAD_TO_BOTTOM)
        `when`(repository.doUpdate()).thenReturn(listOf(1))
        `when`(repository.hasMore()).thenReturn(true)
        `when`(repository.isEmpty()).thenReturn(true, false)
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setRefreshing(true)
        order.verify(view).setLoadingError(null)
        order.verify(view).setLoadStatus(null)
        order.verify(view).setItems(listOf(1))
        order.verify(view).setRefreshing(false)
        // update
        order.verify(view).setLoadStatus(LoadStatus.LOADING)
        order.verify(view).setItems(listOf(1))
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()
    }


    @Test
    fun `更新测试`() {
        `when`(repository.get()).thenReturn(listOf(1))
        `when`(repository.update(any(), any())).thenReturn(ScrollXUpdateAction.LOAD_TO_BOTTOM)
        `when`(repository.doUpdate()).thenReturn(listOf(1))
        `when`(repository.hasMore()).thenReturn(true)
        `when`(repository.isEmpty()).thenReturn(true, false)
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setRefreshing(false)
        // update...
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        // NOTHING 直接退出并清除刷新状态
        `when`(repository.update(any(), any())).thenReturn(ScrollXUpdateAction.NOTHING)
        presenter.runBlocking {
            presenter.update(0, 0)
        }
        assertThrows<VerificationInOrderFailure> {
            order.verify(view).setItems(any())
        }
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        // UPDATE 不更改底部加载状态
        `when`(repository.update(any(), any())).thenReturn(ScrollXUpdateAction.UPDATE)
        presenter.runBlocking {
            presenter.update(0, 0)
        }
        // 不更改底部加载状态
        assertThrows<VerificationInOrderFailure> {
            order.verify(view).setLoadStatus(any())
        }
        order.verify(view).setItems(any())
        order.verify(view).setRefreshing(false)

        // LOAD_TO_TOP 同样不更改底部加载状态
        `when`(repository.update(any(), any())).thenReturn(ScrollXUpdateAction.LOAD_TO_TOP)
        presenter.runBlocking {
            presenter.update(0, 0)
        }
        // 不更改底部加载状态
        assertThrows<VerificationInOrderFailure> {
            order.verify(view).setLoadStatus(any())
        }
        order.verify(view).setItems(any())
        order.verify(view).setRefreshing(false)

        // LOAD_TO_BOTTOM 更改底部加载状态
        `when`(repository.update(any(), any())).thenReturn(ScrollXUpdateAction.LOAD_TO_BOTTOM)
        presenter.runBlocking {
            presenter.update(0, 0)
        }
        order.verify(view).setLoadStatus(LoadStatus.LOADING)
        order.verify(view).setItems(any())
        order.verify(view).setRefreshing(false)

        // 加载完毕
        `when`(repository.doUpdate()).thenReturn(listOf(1))
        `when`(repository.hasMore()).thenReturn(true, false)
        presenter.runBlocking {
            presenter.update(0, 0)
        }
        order.verify(view).setLoadStatus(LoadStatus.LOADING)
        order.verify(view).setItems(any())
        order.verify(view).setLoadStatus(LoadStatus.DONE)
        order.verify(view).setRefreshing(false)
        presenter.runBlocking {
            presenter.update(0, 0)
        }
        // 加载完毕后，不再更改底部加载状态
        order.verify(view, never()).setLoadStatus(LoadStatus.LOADING)
        order.verify(view, never()).setLoadStatus(null)

        order.verify(view).setItems(any())
        order.verify(view).setLoadStatus(LoadStatus.DONE)
        order.verify(view).setRefreshing(false)
    }

    @Test
    fun `刷新测试`() {
        `when`(repository.get()).thenReturn(listOf(1))
        `when`(repository.update(any(), any())).thenReturn(ScrollXUpdateAction.LOAD_TO_BOTTOM)
        `when`(repository.doUpdate()).thenThrow(RuntimeException())
            .thenReturn(listOf(1))
        `when`(repository.hasMore()).thenReturn(true)
        `when`(repository.isEmpty()).thenReturn(true, false)
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setRefreshing(false)
        // update...
        order.verify(repository).doUpdate()
        order.verify(view).setLoadingError(any())
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        // 开始刷新
        presenter.runBlocking {
            presenter.refresh()
        }
        order.verify(view).setRefreshing(true)
        order.verify(repository).refresh()
        order.verify(view, never()).setLoadStatus(anyOrNull())
        order.verify(view).setItems(listOf(1))
        // 正确响应后应当清除错误信息
        order.verify(view).setLoadingError(null)

        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()
    }

}