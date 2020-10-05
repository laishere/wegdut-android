package com.wegdut.wegdut.room.v1.edu

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.wegdut.wegdut.data.edu.Term
import com.wegdut.wegdut.room.DateConverter
import java.util.*

@Entity(tableName = "term", indices = [Index("start", "end")])
@TypeConverters(DateConverter::class)
data class TermEntity(
    @PrimaryKey
    val term: String,
    val termName: String,
    val yearName: String,
    val start: Date,
    val end: Date
) {
    fun toTerm() = Term(term, termName, yearName, start, end)

    companion object {
        fun Term.toEntity() = TermEntity(term, termName, yearName, start, end)
    }
}