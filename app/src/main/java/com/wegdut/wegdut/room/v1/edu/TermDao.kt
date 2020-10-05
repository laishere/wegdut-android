package com.wegdut.wegdut.room.v1.edu

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TermDao {
    @Query("select * from term where start <= datetime('now') and datetime('now') <= `end`")
    fun getCurrentTerm(): TermEntity?

    @Query("select 1 from term limit 1")
    fun hasTerm(): Int?

    @Query("select * from term where start > datetime('now') order by start limit 1")
    fun getNextTerm(): TermEntity?

    @Query("select * from term order by start desc")
    fun getAllTerms(): List<TermEntity>

    @Query("delete from term")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(terms: List<TermEntity>)
}