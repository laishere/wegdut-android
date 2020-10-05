package com.wegdut.wegdut

import java.net.ConnectException
import java.net.SocketTimeoutException

class MyException(message: String?, cause: Throwable? = null) : Exception(message, cause) {

    fun getError(err: String) = message ?: err

    companion object {
        fun handle(e: Throwable, err: String = "发生错误"): String {
            return when (e) {
                is MyException -> e.getError(err)
                is SocketTimeoutException -> "连接超时"
                is ConnectException -> "无连接"
                else -> err
            }
        }
    }
}