package com.wegdut.wegdut.ui.exam_score

import com.wegdut.wegdut.data.edu.Term
import com.wegdut.wegdut.ui.BasePresenter

class ExamScoreActivityContract {
    interface View {
        fun setLoading(loading: Boolean)
        fun setTerms(terms: List<Term>)
        fun setError(error: String?)
    }

    abstract class Presenter : BasePresenter<View>()
}