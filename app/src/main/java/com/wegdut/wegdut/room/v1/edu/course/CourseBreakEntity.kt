package com.wegdut.wegdut.room.v1.edu.course

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wegdut.wegdut.room.DateConverter
import java.util.*

@Entity(tableName = "course_break", indices = [Index("term")])
@TypeConverters(DateConverter::class)
data class CourseBreakEntity(
    @PrimaryKey
    val date: Date,
    val term: String
)