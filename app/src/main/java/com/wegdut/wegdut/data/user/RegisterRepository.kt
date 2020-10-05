package com.wegdut.wegdut.data.user

interface RegisterRepository {
    fun registerByEmail(dto: EmailRegisterDto): Long
    fun registerByStudentAccount(dto: StudentRegisterDto): Long
}