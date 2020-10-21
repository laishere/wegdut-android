package com.wegdut.wegdut.data.library_code

import com.wegdut.wegdut.data.user.StudentDto

interface LibraryCodeRepository {
    fun getStudentFromCache(): StudentDto
    fun getStudentFromApi(): StudentDto
}