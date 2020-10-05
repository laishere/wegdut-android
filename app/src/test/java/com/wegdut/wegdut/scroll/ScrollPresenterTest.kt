package com.wegdut.wegdut.scroll

import com.nhaarman.mockitokotlin2.*
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.PresenterTestUtils.runBlocking
import com.wegdut.wegdut.data.LoadStatus
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ScrollPresenterTest {

    @Mock
    lateinit var view: ScrollContract.View<Int>

    @Mock
    lateinit var repository: ScrollRepository<Int>

    private val presenter = ScrollPresenter<Int, ScrollContract.View<Int>>()
    private lateinit var order: InOrder

    @Before
    fun setup() {
        MyLog.isTesting = true
        MockitoAnnotations.initMocks(this)
        order = inOrder(view, repository)
        presenter.repository = repository
        presenter.start()
    }

    @Test
    fun `订阅测试`() {
        `when`(repository.get()).thenReturn(listOf(1))

        // 首次订阅
        `when`(repository.hasMore()).thenReturn(true)
        `when`(repository.isEmpty()).thenReturn(true, false)
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setError(null)
        order.verify(view).setRefreshing(true)
        order.verify(view).setLoadStatus(null)
        order.verify(repository).get()
        order.verify(view).setItems(eq(listOf(1)))
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        // 再次订阅，repository已有内容
        presenter.runBlocking {
            presenter.unsubscribe()
            presenter.subscribe(view)
        }
        order.verify(view).setError(null)
        order.verify(repository).get()
        order.verify(view).setItems(eq(listOf(1)))
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        verify(view, times(3)).setRefreshing(any())
        verify(view).setLoadStatus(null)

        // 订阅出错
        `when`(repository.get()).thenThrow(RuntimeException())
        presenter.runBlocking {
            presenter.unsubscribe()
            presenter.subscribe(view)
        }
        order.verify(view).setError(null)
        order.verify(repository).get()
        order.verify(view).setError(any())
        order.verify(view).setRefreshing(false)
    }

    @Test
    fun `加载更多测试`() {
        `when`(repository.get()).thenReturn(listOf(1))
        `when`(repository.hasMore()).thenReturn(true)
        `when`(repository.isEmpty()).thenReturn(true, false)
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        // 订阅之后
        presenter.runBlocking {
            presenter.loadMore()
        }
        order.verify(view).setError(null)
        order.verify(view).setLoadStatus(LoadStatus.LOADING)
        order.verify(repository).loadMore()
        order.verify(repository).get()
        order.verify(view).setItems(any())
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        // 加载完毕
        `when`(repository.hasMore()).thenReturn(true, false)
        presenter.runBlocking {
            presenter.loadMore()
        }
        order.verify(view).setError(null)
        order.verify(view).setLoadStatus(LoadStatus.LOADING)
        order.verify(repository).loadMore()
        order.verify(repository).get()
        order.verify(view).setItems(any())
        order.verify(view).setLoadStatus(LoadStatus.DONE)
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        // 加载完毕之后
        presenter.runBlocking {
            presenter.loadMore()
        }
        order.verify(repository).hasMore()
        order.verifyNoMoreInteractions()

        // 加载出错
        `when`(repository.hasMore()).thenReturn(true)
        `when`(repository.loadMore()).thenThrow(RuntimeException())
        presenter.runBlocking {
            presenter.loadMore()
        }
        order.verify(view).setError(null)
        order.verify(view).setLoadStatus(LoadStatus.LOADING)
        order.verify(repository).loadMore()
        order.verify(view).setError(any())
        order.verify(view).setLoadStatus(null)
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `刷新测试`() {
        `when`(repository.get()).thenReturn(listOf(1))
        `when`(repository.hasMore()).thenReturn(true)
        `when`(repository.isEmpty()).thenReturn(true, false)
        presenter.runBlocking {
            presenter.subscribe(view)
        }
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        // 正常刷新
        `when`(repository.isEmpty()).thenReturn(true, false)
        presenter.runBlocking {
            presenter.refresh()
        }
        order.verify(view).setError(null)
        order.verify(view).setRefreshing(true)
        order.verify(view).setLoadStatus(null)
        order.verify(repository).get()
        order.verify(view).setItems(eq(listOf(1)))
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        // 刷新出错后清空内容
        `when`(repository.get()).thenThrow(RuntimeException())
        `when`(repository.isEmpty()).thenReturn(true, false)
        presenter.clearItemsAfterRefreshingFailed = true
        presenter.runBlocking {
            presenter.refresh()
        }
        order.verify(view).setError(null)
        order.verify(view).setRefreshing(true)
        order.verify(view).setLoadStatus(null)
        order.verify(repository).get()
        order.verify(view).setItems(null) // 清空内容
        order.verify(view).setError(any())
        order.verify(view).setLoadStatus(null)
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        // 刷新出错后不清空内容
        `when`(repository.isEmpty()).thenReturn(true, false)
        presenter.clearItemsAfterRefreshingFailed = false
        presenter.runBlocking {
            presenter.refresh()
        }
        order.verify(view).setError(null)
        order.verify(view).setRefreshing(true)
        order.verify(view).setLoadStatus(null)
        order.verify(repository).get()
        order.verify(view).setError(any())
        order.verify(view).setLoadStatus(null)
        order.verify(view).setRefreshing(false)
        order.verifyNoMoreInteractions()

        verify(view).setItems(null) // 仅一次清空
    }
}