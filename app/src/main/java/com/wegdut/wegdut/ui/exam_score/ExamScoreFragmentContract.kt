package com.wegdut.wegdut.ui.exam_score

import com.wegdut.wegdut.data.edu.exam_score.ExamScore
import com.wegdut.wegdut.ui.BasePresenter

class ExamScoreFragmentContract {
    interface View {
        fun setExamScores(scores: List<ExamScore>)
        fun setLoading(loading: Boolean)
        fun setError(error: String?)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun getExamScores(term: String)
        abstract fun refresh()
    }
}