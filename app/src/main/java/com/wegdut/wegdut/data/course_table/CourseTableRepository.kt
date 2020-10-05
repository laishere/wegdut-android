package com.wegdut.wegdut.data.course_table

import com.wegdut.wegdut.data.edu.Term

interface CourseTableRepository {
    fun getCourseFromCache(term: String): CourseTableData
    fun getCourseFromApi(term: String): CourseTableData
    fun getTermsFromCache(): List<Term>
    fun getTermsFromApi(): List<Term>
    fun refresh()
}