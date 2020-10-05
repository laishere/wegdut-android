package com.wegdut.wegdut.room.v1.edu.course

import androidx.room.*
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.data.edu.course.DayCourse
import com.wegdut.wegdut.room.DateConverter
import java.util.*

@Entity(tableName = "course", indices = [Index("term", "week"), Index("date")])
@TypeConverters(DateConverter::class, CoursesConverter::class)
data class DayCourseEntity(
    @PrimaryKey
    var date: Date,
    var term: String,
    var week: Int,
    var weekDay: Int,
    var label: String,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val course: List<Course>
) {
    fun toDayCourse() = DayCourse(date, week, weekDay, label, course)

    companion object {
        fun DayCourse.toEntity(term: String) = DayCourseEntity(
            date, term, week, weekDay, label, course
        )
    }
}