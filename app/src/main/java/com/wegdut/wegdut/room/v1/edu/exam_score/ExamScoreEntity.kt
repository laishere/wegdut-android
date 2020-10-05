package com.wegdut.wegdut.room.v1.edu.exam_score

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.wegdut.wegdut.data.edu.exam_score.ExamScore

@Entity(tableName = "exam_score", indices = [Index("term")])
data class ExamScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val term: String,
    val number: String,
    val name: String,
    val subject: String,
    val score: String,
    val gradePoint: String,
    val studyTime: String,
    val credit: String,
    val examType: String,
    val scoreType: String,
    val isValid: String
) {
    fun toExamScore() = ExamScore(
        term, number, name, subject, score, gradePoint,
        studyTime, credit, examType, scoreType, isValid
    )

    companion object {
        fun ExamScore.toEntity() = ExamScoreEntity(
            0, term, number, name, subject, score, gradePoint,
            studyTime, credit, examType, scoreType, isValid
        )
    }
}