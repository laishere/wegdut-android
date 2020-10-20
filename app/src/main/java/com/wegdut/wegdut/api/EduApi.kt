package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.edu.Term
import com.wegdut.wegdut.data.edu.course.CourseTableDto
import com.wegdut.wegdut.data.edu.exam_plan.ExamPlanDto
import com.wegdut.wegdut.data.edu.exam_score.ExamScoreDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EduApi {
    @GET("/course?v2")
    fun getCourse(@Query("term") term: String?): Call<ResultWrapper<CourseTableDto>>

    @GET("/exam-plan")
    fun getExamPlan(): Call<ResultWrapper<List<ExamPlanDto>>>

    @GET("/exam-score")
    fun getExamScore(@Query("term") term: String): Call<ResultWrapper<List<ExamScoreDto>>>

    @GET("/term")
    fun getTerms(): Call<ResultWrapper<List<Term>>>
}