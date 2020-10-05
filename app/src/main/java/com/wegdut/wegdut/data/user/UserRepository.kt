package com.wegdut.wegdut.data.user

import com.wegdut.wegdut.data.edu.exam_plan.ExamPlan

interface UserRepository {
    fun getExamPlanFromApi(): List<ExamPlan>
    fun getExamPlanFromCache(): List<ExamPlan>
    fun getUser()
    fun refresh()
}