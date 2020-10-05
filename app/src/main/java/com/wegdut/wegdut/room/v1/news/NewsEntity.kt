package com.wegdut.wegdut.room.v1.news

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wegdut.wegdut.data.news.News
import com.wegdut.wegdut.room.DateConverter
import java.util.*

@Entity(tableName = "notification")
@TypeConverters(DateConverter::class)
data class NewsEntity(
    @PrimaryKey
    val id: Long,
    val category: String,
    val department: String,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val title: String,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val contentAbstract: String,
    val postTime: Date,
    val studentRelated: Boolean = false
) {
    companion object {
        fun News.toEntity() = NewsEntity(
            id, category, department, title, contentAbstract, postTime, studentRelated
        )
    }

    fun toNews() = News(
        id, category, department, title, contentAbstract, postTime, studentRelated
    )
}