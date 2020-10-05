package com.wegdut.wegdut.data.user

import com.wegdut.wegdut.api.EduApi
import com.wegdut.wegdut.data.edu.exam_plan.ExamPlan
import com.wegdut.wegdut.room.v1.kv_storage.KVDao
import com.wegdut.wegdut.room.v1.kv_storage.KVEntity
import com.wegdut.wegdut.utils.ApiUtils
import com.wegdut.wegdut.utils.ApiUtils.extract
import java.util.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor() : UserRepository {

    @Inject
    lateinit var loginRepository: LoginRepository

    @Inject
    lateinit var eduApi: EduApi

    @Inject
    lateinit var kvDao: KVDao
    private var examApiCache: List<ExamPlan>? = null
    private var examDBCache: List<ExamPlan>? = null

    companion object {
        const val EXAM_PLAN_KEY = "EXAM_PLAN"
    }

    override fun getExamPlanFromApi(): List<ExamPlan> {
        if (examApiCache != null) return examApiCache!!
        val plans = eduApi.getExamPlan().extract().map { it.toExamPlan() }
        val json = ApiUtils.gson.toJson(plans)
        val kv = KVEntity(EXAM_PLAN_KEY, json, Date())
        kvDao.save(kv)
        examApiCache = plans
        return examApiCache!!
    }

    override fun getExamPlanFromCache(): List<ExamPlan> {
        if (examDBCache != null) return examDBCache!!
        val kv = kvDao.get(EXAM_PLAN_KEY) ?: return emptyList()
        val plans = ApiUtils.gson.fromJson(kv.value, Array<ExamPlan>::class.java).toList()
        examDBCache = plans
        return examDBCache!!
    }

    override fun getUser() {
        loginRepository.getCurrentUser()
    }

    override fun refresh() {
        examApiCache = null
        examDBCache = null
    }
}