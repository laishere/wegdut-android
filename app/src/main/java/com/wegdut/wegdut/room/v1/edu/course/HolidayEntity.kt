package com.wegdut.wegdut.room.v1.edu.course

import androidx.room.Entity
import androidx.room.Index
import androidx.room.TypeConverters
import com.wegdut.wegdut.room.DateConverter
import java.util.*

@Entity(tableName = "holiday", primaryKeys = ["start", "end"], indices = [Index("term")])
@TypeConverters(DateConverter::class)
data class HolidayEntity(
    val start: Date,
    val end: Date,
    val holidayDesc: String,
    val term: String
)