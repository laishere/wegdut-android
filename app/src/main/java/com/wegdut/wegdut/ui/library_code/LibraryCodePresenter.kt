package com.wegdut.wegdut.ui.library_code

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.library_code.LibraryCodeRepository
import javax.inject.Inject

class LibraryCodePresenter @Inject constructor(): LibraryCodeContract.Presenter() {

    @Inject lateinit var repository: LibraryCodeRepository

    private fun getFromCache() {
        launch {
            view?.setError(null)
            view?.setLoading(true)
            tryIt({
                val studentDto = io {
                    repository.getStudentFromCache()
                }
                view?.setStudent(studentDto)
                view?.setLoading(false)
            })
            getFromApi()
        }
    }

    private fun getFromApi() {
        launch {
            tryIt({
                val studentDto = io {
                    repository.getStudentFromApi()
                }
                view?.setStudent(studentDto)
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setError(err)
            }
            view?.setLoading(false)
        }
    }

    override fun subscribe(view: LibraryCodeContract.View) {
        super.subscribe(view)
        getFromCache()
    }
}