package com.wegdut.wegdut.room

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateConverter {
    private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    @TypeConverter
    fun fromDate(date: Date?): String? {
        return if (date == null) null
        else formatter.format(date)
    }

    @TypeConverter
    fun toDate(str: String?): Date? {
        return if (str == null) null
        else formatter.parse(str)
    }
}