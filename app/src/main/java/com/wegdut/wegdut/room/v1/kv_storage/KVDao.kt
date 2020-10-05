package com.wegdut.wegdut.room.v1.kv_storage

import androidx.room.*

@Dao
interface KVDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(kv: KVEntity)

    @Query("select * from log where `key`=:name")
    fun get(name: String): KVEntity?

    @Delete
    fun delete(kv: KVEntity)

    @Query("delete from log where `key` = :key")
    fun delete(key: String)
}