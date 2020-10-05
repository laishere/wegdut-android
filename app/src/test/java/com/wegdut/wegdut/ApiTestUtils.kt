package com.wegdut.wegdut

import com.wegdut.wegdut.data.ResultWrapper
import okhttp3.ResponseBody
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import retrofit2.Call
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
object ApiTestUtils {
    fun <T> okCall(data: T): Call<T> {
        val call = Mockito.mock(Call::class.java)
        `when`(call.execute()).thenReturn(Response.success(data))
        return call as Call<T>
    }

    fun <T> okWrapper(data: T): Call<ResultWrapper<T>> {
        val call = Mockito.mock(Call::class.java)
        val wrapper = ResultWrapper(data = data)
        `when`(call.execute()).thenReturn(Response.success(wrapper))
        return call as Call<ResultWrapper<T>>
    }

    fun <T> errorHttp(code: Int): Call<T> {
        val call = Mockito.mock(Call::class.java)
        val rsp: Response<T> = Response.error(code, Mockito.mock(ResponseBody::class.java))
        `when`(call.execute()).thenReturn(rsp)
        return call as Call<T>
    }

    fun <T> errorWrapper(message: String, code: Int = 0): Call<ResultWrapper<T>> {
        val call = Mockito.mock(Call::class.java)
        val data: T? = null
        val wrapper = ResultWrapper("err", data, code, message)
        `when`(call.execute()).thenReturn(Response.success(wrapper))
        return call as Call<ResultWrapper<T>>
    }
}