package com.wegdut.wegdut.data.edu.exam_score

interface ExamScoreRepository {
    fun getScoresFromCache(term: String): List<ExamScore>
    fun getScoresFromApi(term: String): List<ExamScore>
    fun refresh()
}