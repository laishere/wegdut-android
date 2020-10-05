package com.wegdut.wegdut.data.edu.exam_score

import com.wegdut.wegdut.api.EduApi
import com.wegdut.wegdut.room.v1.edu.exam_score.ExamScoreDao
import com.wegdut.wegdut.room.v1.edu.exam_score.ExamScoreEntity.Companion.toEntity
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class ExamScoreRepositoryImpl @Inject constructor() : ExamScoreRepository {
    private var scoresCache: List<ExamScore>? = null
    private var scoresApiCache: List<ExamScore>? = null
    private var cacheTerm: String? = null
    private var apiTerm: String? = null

    @Inject
    lateinit var examScoreDao: ExamScoreDao

    @Inject
    lateinit var eduApi: EduApi

    override fun getScoresFromCache(term: String): List<ExamScore> {
        if (cacheTerm == term)
            return scoresCache!!
        val scores = examScoreDao.getScoresByTerm(term)
        scoresCache = scores.map { it.toExamScore() }
        cacheTerm = term
        return scoresCache!!
    }

    override fun getScoresFromApi(term: String): List<ExamScore> {
        if (apiTerm == term)
            return scoresApiCache!!
        val dto = eduApi.getExamScore(term).extract()
        val scores = dto.map { it.toExamScore() }
        val cache = scores.map { it.toEntity() }
        examScoreDao.deleteByTerm(term)
        examScoreDao.saveAll(cache)
        scoresApiCache = scores
        apiTerm = term
        return scoresApiCache!!
    }

    override fun refresh() {
        cacheTerm = null
        apiTerm = null
        scoresCache = null
        scoresApiCache = null
    }
}