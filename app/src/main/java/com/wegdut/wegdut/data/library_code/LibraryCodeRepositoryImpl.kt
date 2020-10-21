package com.wegdut.wegdut.data.library_code

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.api.AccountInfoApi
import com.wegdut.wegdut.data.user.StudentDto
import com.wegdut.wegdut.room.v1.kv_storage.KVDao
import com.wegdut.wegdut.utils.ApiUtils
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class LibraryCodeRepositoryImpl @Inject constructor(): LibraryCodeRepository {

    @Inject lateinit var kvDao: KVDao
    @Inject lateinit var accountInfoApi: AccountInfoApi
    private var localCache: StudentDto? = null
    private var apiCache: StudentDto? = null

    companion object {
        const val STUDENT_KEY = "STUDENT"
    }

    override fun getStudentFromCache(): StudentDto {
        if (localCache != null) return localCache!!
        val kv = kvDao.get(STUDENT_KEY) ?: throw MyException("没有本地缓存")
        localCache = ApiUtils.gson.fromJson(kv.value, StudentDto::class.java)
        return localCache!!
    }

    override fun getStudentFromApi(): StudentDto {
        if (apiCache != null) return apiCache!!
        apiCache = accountInfoApi.getStudent().extract()
        localCache = apiCache
        return apiCache!!
    }

}