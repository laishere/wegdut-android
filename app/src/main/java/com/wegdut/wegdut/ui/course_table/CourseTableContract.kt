package com.wegdut.wegdut.ui.course_table

import com.wegdut.wegdut.data.course_table.CourseTableData
import com.wegdut.wegdut.data.edu.Term
import com.wegdut.wegdut.ui.BasePresenter

class CourseTableContract {
    interface View {
        fun setCourseLoading(loading: Boolean)
        fun setCourseData(data: CourseTableData)
        fun setCourseLoadingError(error: String?)
        fun setTermLoading(loading: Boolean)
        fun setTermLoadingError(error: String?)
        fun setTerms(terms: List<Term>)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun getCourseData(term: String)
    }
}