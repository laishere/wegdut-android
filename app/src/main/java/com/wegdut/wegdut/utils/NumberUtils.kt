package com.wegdut.wegdut.utils

object NumberUtils {
    fun format(num: Int, none: String = "0", unit: String = ""): String {
        val a = when {
            num == 0 -> none
            num > 10000 -> "%.1f万".format(num / 10000f)
            else -> "$num"
        }
        return "$a$unit"
    }
}