package com.wegdut.wegdut.room.v1.edu.course

import androidx.room.*
import com.wegdut.wegdut.room.DateConverter
import java.util.*

@Dao
@TypeConverters(DateConverter::class)
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(dayCourse: List<DayCourseEntity>)

    @Query("select * from course where date=:date")
    fun get(date: Date): DayCourseEntity?

    @Query("select * from course where term = :term and week = :week")
    fun getByTermAndWeek(term: String, week: Int): List<DayCourseEntity>

    @Query("select count(1) from course")
    fun count(): Int

    @Query("delete from course where term = :term")
    fun deleteByTerm(term: String)
}