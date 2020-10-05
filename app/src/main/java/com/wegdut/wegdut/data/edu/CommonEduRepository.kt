package com.wegdut.wegdut.data.edu

interface CommonEduRepository {

    fun getTermsFromCache(): List<Term>

    fun getTermsFromApi(): List<Term>
}