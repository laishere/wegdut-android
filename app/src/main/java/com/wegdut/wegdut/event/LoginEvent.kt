package com.wegdut.wegdut.event

data class LoginEvent(
    val status: Status
) {
    enum class Status {
        LOGIN, LOGOUT
    }
}