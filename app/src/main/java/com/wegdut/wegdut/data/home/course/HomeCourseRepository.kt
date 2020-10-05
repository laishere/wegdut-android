package com.wegdut.wegdut.data.home.course

interface HomeCourseRepository {
    fun getFromCache(): HomeCourse
    fun getFromApi(): HomeCourse
    fun refresh()
}