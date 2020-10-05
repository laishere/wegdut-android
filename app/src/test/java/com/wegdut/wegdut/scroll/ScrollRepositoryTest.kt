package com.wegdut.wegdut.scroll

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.inOrder
import com.wegdut.wegdut.data.PageWrapper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ScrollRepositoryTest {

    @Mock
    lateinit var toImpl: ToImpl

    @InjectMocks
    private val repository = object : BaseScrollRepository<Int>() {
        lateinit var toImpl: ToImpl
        override fun get(count: Int, offset: Int, marker: String?): PageWrapper<Int> {
            return toImpl.get(count, offset, marker)
        }
    }
    private lateinit var order: InOrder

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        order = inOrder(toImpl)
    }

    @Test
    fun `加载内容测试`() {
        val items = listOf(1)
        val page = PageWrapper(items, false, "")
        `when`(toImpl.get(any(), any(), anyOrNull())).thenReturn(page)
        assert(repository.isEmpty())
        assert(repository.hasMore())

        // 首次加载
        val r = repository.get()
        assertEquals(items, r)
        assert(!repository.isEmpty())
        assert(repository.hasMore())
        order.verify(toImpl).get(any(), any(), eq(null))
        order.verifyNoMoreInteractions()

        // 再次加载时缓存应该起作用
        repository.get()
        order.verifyNoMoreInteractions()

        // 刷新
        repository.refresh()
        assert(repository.isEmpty())
        repository.get()
        assert(!repository.isEmpty())
        order.verify(toImpl).get(any(), any(), eq(null))
        order.verifyNoMoreInteractions()
    }

    @Test
    fun `加载更多内容测试`() {
        val items = listOf(1)
        val marker = "marker"
        val page = PageWrapper(items, false, marker)
        `when`(toImpl.get(any(), any(), anyOrNull())).thenReturn(page)
        var r = repository.get()
        assertEquals(items, r)
        order.verify(toImpl).get(any(), any(), eq(null))
        order.verifyNoMoreInteractions()

        // 加载更多时应当有marker
        repository.loadMore()
        r = repository.get()
        assertEquals(listOf(1, 1), r)
        order.verify(toImpl).get(any(), any(), eq(marker))
        order.verifyNoMoreInteractions()

        // 刷新后，marker应当变为null
        repository.refresh()
        repository.get()
        order.verify(toImpl).get(any(), any(), eq(null))
        order.verifyNoMoreInteractions()
    }

    interface ToImpl {
        fun get(count: Int, offset: Int, marker: String?): PageWrapper<Int>
    }
}