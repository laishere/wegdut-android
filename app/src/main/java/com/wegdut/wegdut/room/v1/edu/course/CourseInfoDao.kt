package com.wegdut.wegdut.room.v1.edu.course

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CourseInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(infoEntity: CourseInfoEntity)

    @Query("select * from course_info where term = :term")
    fun get(term: String): CourseInfoEntity?
}