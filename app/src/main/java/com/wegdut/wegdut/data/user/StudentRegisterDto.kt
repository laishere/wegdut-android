package com.wegdut.wegdut.data.user

data class StudentRegisterDto(
    val name: String,
    val password: String,
    val nickname: String,
    val studentNumber: String,
    val studentPassword: String
)