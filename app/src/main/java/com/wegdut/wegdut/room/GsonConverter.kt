package com.wegdut.wegdut.room

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.wegdut.wegdut.utils.ApiUtils

abstract class GsonConverter<T> {

    abstract val typeToken: TypeToken<T>

    @TypeConverter
    open fun toJson(obj: T?): String? {
        if (obj == null) return null
        return ApiUtils.gson.toJson(obj)
    }

    @TypeConverter
    open fun fromJson(json: String?): T? {
        if (json == null) return null
        return ApiUtils.gson.fromJson(json, typeToken.type)
    }
}