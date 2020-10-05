package com.wegdut.wegdut.data.edu

import com.wegdut.wegdut.api.EduApi
import com.wegdut.wegdut.room.v1.edu.TermDao
import com.wegdut.wegdut.room.v1.edu.TermEntity.Companion.toEntity
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class CommonEduRepositoryImpl @Inject constructor() : CommonEduRepository {

    @Inject
    lateinit var eduApi: EduApi

    @Inject
    lateinit var termDao: TermDao

    override fun getTermsFromCache(): List<Term> = termDao.getAllTerms().map { it.toTerm() }

    override fun getTermsFromApi(): List<Term> {
        val terms = eduApi.getTerms().extract()
        val termEntities = terms.map { it.toEntity() }
        termDao.deleteAll()
        termDao.saveAll(termEntities)
        return terms
    }
}