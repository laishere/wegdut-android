package com.wegdut.wegdut.room

import androidx.room.TypeConverter

/**
 * 避免返回为null的列表
 */
abstract class ListConverter<T> : GsonConverter<List<T>>() {
    @TypeConverter
    override fun fromJson(json: String?): List<T> {
        return super.fromJson(json) ?: emptyList()
    }
}