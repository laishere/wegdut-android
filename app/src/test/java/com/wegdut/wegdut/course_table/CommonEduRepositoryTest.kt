package com.wegdut.wegdut.course_table

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.wegdut.wegdut.ApiTestUtils
import com.wegdut.wegdut.api.EduApi
import com.wegdut.wegdut.data.edu.CommonEduRepositoryImpl
import com.wegdut.wegdut.data.edu.Term
import com.wegdut.wegdut.room.v1.edu.TermDao
import com.wegdut.wegdut.room.v1.edu.TermEntity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.*

class CommonEduRepositoryTest {

    @Mock
    lateinit var eduApi: EduApi

    @Mock
    lateinit var termDao: TermDao

    private val repository = CommonEduRepositoryImpl()
    private lateinit var order: InOrder

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        order = inOrder(eduApi, termDao)
        repository.eduApi = eduApi
        repository.termDao = termDao
    }

    @Test
    fun `从本地缓存中获取学期信息测试`() {
        val date = Date()
        val termEntities = listOf(
            TermEntity("term", "name", "year", date, date)
        )
        val terms = termEntities.map { it.toTerm() }
        `when`(termDao.getAllTerms()).thenReturn(termEntities)
        val r = repository.getTermsFromCache()
        assertEquals(terms, r)
    }

    @Test
    fun `从API中获取学期信息测试`() {
        val list = listOf(
            Term("term", "name", "year", Date(), Date())
        )
        val call = ApiTestUtils.okWrapper(list)
        `when`(eduApi.getTerms()).thenReturn(call)
        val r = repository.getTermsFromApi()
        assertEquals(list, r)
        order.verify(eduApi).getTerms()
        order.verify(termDao).deleteAll()
        order.verify(termDao).saveAll(any())
        order.verifyNoMoreInteractions()
    }

}