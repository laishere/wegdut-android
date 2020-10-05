package com.wegdut.wegdut.room.v1.edu.exam_score

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExamScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(scores: List<ExamScoreEntity>)

    @Query("select * from exam_score where term = :term order by id asc")
    fun getScoresByTerm(term: String): List<ExamScoreEntity>

    @Query("delete from exam_score where term = :term")
    fun deleteByTerm(term: String)
}