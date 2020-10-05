package com.wegdut.wegdut.room.v1.edu.course

import androidx.room.*
import com.wegdut.wegdut.room.DateConverter
import java.util.*

@Dao
@TypeConverters(DateConverter::class)
interface HolidayDao {
    @Query("select * from holiday where start<=:date and `end`>=:date")
    fun findHoliday(date: Date): HolidayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(holidays: List<HolidayEntity>)

    @Query("delete from holiday where term = :term")
    fun deleteByTerm(term: String)
}