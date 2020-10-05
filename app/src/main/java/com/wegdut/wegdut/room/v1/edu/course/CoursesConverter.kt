package com.wegdut.wegdut.room.v1.edu.course

import com.google.gson.reflect.TypeToken
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.room.ListConverter

class CoursesConverter : ListConverter<Course>() {
    override val typeToken: TypeToken<List<Course>> = object : TypeToken<List<Course>>() {}
}