package com.wegdut.wegdut.data.user

interface LoginRepository {
    fun getCurrentUser()
    fun login(username: String, password: String): User
    fun logout()
}