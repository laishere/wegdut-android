package com.wegdut.wegdut.room.v1.edu.course

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wegdut.wegdut.room.DateConverter
import java.util.*

@Entity(tableName = "course_info")
@TypeConverters(DateConverter::class)
data class CourseInfoEntity(
    @PrimaryKey
    val term: String,
    val startDate: Date,
    val totalWeek: Int
)