package com.wegdut.wegdut.room.v1.news

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(entity: NewsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(entity: List<NewsEntity>)

    @Query("select * from notification where category = '校内通知' order by postTime desc limit :count")
    fun getHomeNotification(count: Int): List<NewsEntity>

    @Query("select * from notification where studentRelated = 1 and category = '校内通知' order by postTime desc limit :count")
    fun getHomeNotificationOnlyForStudent(count: Int): List<NewsEntity>

    @Query("select count(1) from notification")
    fun count(): Int

    @Query("delete from notification")
    fun deleteAll()
}