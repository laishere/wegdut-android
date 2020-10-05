package com.wegdut.wegdut.room.v1.kv_storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wegdut.wegdut.room.DateConverter
import java.util.*

@Entity(tableName = "log")
@TypeConverters(DateConverter::class)
data class KVEntity(
    @PrimaryKey
    val key: String,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val value: String,
    val updatedAt: Date
)