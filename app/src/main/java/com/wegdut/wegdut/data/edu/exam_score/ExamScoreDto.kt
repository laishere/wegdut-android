package com.wegdut.wegdut.data.edu.exam_score

data class ExamScoreDto(
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
}