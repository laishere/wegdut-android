package com.wegdut.wegdut.ui.library_code

import com.wegdut.wegdut.data.user.StudentDto
import com.wegdut.wegdut.ui.BasePresenter

class LibraryCodeContract {
    interface View {
        fun setStudent(dto: StudentDto)
        fun setError(error: String?)
        fun setLoading(loading: Boolean)
    }

    abstract class Presenter: BasePresenter<View>()
}