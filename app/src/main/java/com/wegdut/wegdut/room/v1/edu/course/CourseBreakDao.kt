package com.wegdut.wegdut.room.v1.edu.course

import androidx.room.*
import com.wegdut.wegdut.room.DateConverter
import java.util.*

@Dao
@TypeConverters(DateConverter::class)
interface CourseBreakDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(breaks: List<CourseBreakEntity>)

    @Query("select * from course_break where date = :date")
    fun find(date: Date): CourseBreakEntity?

    @Query("delete from course_break where term = :term")
    fun deleteByTerm(term: String)
}