package com.wegdut.wegdut.scrollx

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.inOrder
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.scrollx.ScrollXRequest
import com.wegdut.wegdut.data.scrollx.ScrollXResponse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.`when`

class ScrollXRepositoryTest {

    @Mock
    lateinit var toImpl: ToImpl

    @Captor
    lateinit var scrollXRequestCaptor: ArgumentCaptor<ScrollXRequest>

    @InjectMocks
    private val repository = object : BaseScrollXRepository<Int>() {
        lateinit var toImpl: ToImpl

        override fun getAnchor(item: Int?): String? {
            return item?.toString()
        }

        override fun getFromApi(request: ScrollXRequest): ScrollXResponse<Int> {
            return toImpl.getFromApi(request)
        }

        override fun getFromLocal(limit: Int): List<Int> {
            return emptyList()
        }
    }

    private lateinit var order: InOrder

    @Before
    fun setup() {
        MyLog.isTesting = true
        MockitoAnnotations.initMocks(this)
        order = inOrder(toImpl)
    }

    @Test
    fun `加载测试`() {
        // 当缓存为空时，应当从API中加载
        `when`(toImpl.getFromApi(any())).thenReturn(
            ScrollXResponse(
                listOf(1),
                true,
                listOf(2),
                true
            )
        )
        val getVerify = {
            val r = repository.get()
            assertEquals(listOf(1, 2), r)
            order.verify(toImpl).getFromApi(capture(scrollXRequestCaptor))
            scrollXRequestCaptor.value.run {
                assertEquals(null, topAnchor)
                assertEquals(1, topOffset)
                assertEquals(null, bottomAnchor)
                assertEquals(0, bottomOffset)
            }
            order.verifyNoMoreInteractions()
        }
        getVerify()

        // 再次get将不从API获取
        val r = repository.get()
        assertEquals(listOf(1, 2), r)
        order.verifyNoMoreInteractions()

        // 刷新
        repository.refresh()
        getVerify()
    }

    @Test
    fun `更新测试`() {
        `when`(toImpl.getFromApi(any())).thenReturn(
            ScrollXResponse(
                listOf(3),
                true,
                listOf(4),
                true
            )
        )
        val r = repository.get()
        assertEquals(listOf(3, 4), r)

        // 更新顶部
        `when`(toImpl.getFromApi(any())).thenReturn(
            ScrollXResponse(
                listOf(1, 2),
                true,
                emptyList(),
                true
            )
        )
        repository.updateTop()
        repository.update(0, 0)
        var res = repository.doUpdate()
        assertEquals(listOf(1, 2, 3, 4), res)

        // 从第三个位置开始更新所有
        `when`(toImpl.getFromApi(any())).thenReturn(
            ScrollXResponse(
                listOf(7),
                true,
                listOf(8, 9),
                true
            )
        )
        repository.updateAll()
        repository.update(2, 3)
        res = repository.doUpdate()
        assertEquals(listOf(7, 8, 9), res)
    }


    interface ToImpl {
        fun getFromApi(request: ScrollXRequest): ScrollXResponse<Int>
    }
}