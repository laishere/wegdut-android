package com.wegdut.wegdut.data

data class ResultWrapper<T>(
    val status: String = "ok",
    val data: T,
    val code: Int = 0,
    val msg: String? = null
) {
    fun ok() = status == "ok"
}