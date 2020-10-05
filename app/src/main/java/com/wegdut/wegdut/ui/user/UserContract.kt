package com.wegdut.wegdut.ui.user

import com.wegdut.wegdut.data.edu.exam_plan.ExamPlan
import com.wegdut.wegdut.ui.BasePresenter

class UserContract {
    interface View {
        fun setExamPlan(items: List<ExamPlan>)
        fun setRefreshing(refreshing: Boolean)
        fun setExamPlanError(error: String?)
        fun updateUser()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun refresh()
    }
}