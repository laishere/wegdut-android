package com.wegdut.wegdut.data.user

import com.wegdut.wegdut.api.RegisterApi
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor() : RegisterRepository {
    @Inject
    lateinit var api: RegisterApi

    override fun registerByEmail(dto: EmailRegisterDto): Long {
        return api.register(dto).extract()!!
    }

    override fun registerByStudentAccount(dto: StudentRegisterDto): Long {
        return api.registerStudent(dto).extract()!!
    }
}