package com.wegdut.wegdut.user

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.wegdut.wegdut.ApiTestUtils
import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.api.EduApi
import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.edu.exam_plan.ExamPlanDto
import com.wegdut.wegdut.data.user.LoginRepository
import com.wegdut.wegdut.data.user.UserRepositoryImpl
import com.wegdut.wegdut.room.v1.kv_storage.KVDao
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Call

class UserRepositoryTest {
    private val userRepository = UserRepositoryImpl()

    @Mock
    private lateinit var kvDao: KVDao

    @Mock
    private lateinit var eduApi: EduApi

    @Mock
    private lateinit var loginRepository: LoginRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        userRepository.loginRepository = loginRepository
        userRepository.eduApi = eduApi
        userRepository.kvDao = kvDao
    }

    @Test
    fun `api正常获取考试安排测试`() {
        val plans = emptyList<ExamPlanDto>()
        val call = ApiTestUtils.okWrapper(plans)
        `when`(eduApi.getExamPlan()).thenReturn(call)
        val res = userRepository.getExamPlanFromApi()
        assertNotNull(res)
        verify(kvDao).save(any())
    }

    @Test
    fun `http异常获取考试安排测试`() {
        val call: Call<ResultWrapper<List<ExamPlanDto>>> = ApiTestUtils.errorHttp(404)
        `when`(eduApi.getExamPlan()).thenReturn(call)
        assertThrows<MyException> {
            userRepository.getExamPlanFromApi()
        }
        verifyZeroInteractions(kvDao)
    }

    @Test
    fun `http正常而api异常获取考试安排测试`() {
        val error = "error"
        val call: Call<ResultWrapper<List<ExamPlanDto>>> = ApiTestUtils.errorWrapper(error)
        `when`(eduApi.getExamPlan()).thenReturn(call)
        assertThrows<MyException>(error) {
            userRepository.getExamPlanFromApi()
        }
        verifyZeroInteractions(kvDao)
    }

}