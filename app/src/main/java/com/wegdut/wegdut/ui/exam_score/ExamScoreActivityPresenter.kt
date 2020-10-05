package com.wegdut.wegdut.ui.exam_score

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.edu.CommonEduRepository
import javax.inject.Inject

class ExamScoreActivityPresenter @Inject constructor() : ExamScoreActivityContract.Presenter() {
    @Inject
    lateinit var commonEduRepository: CommonEduRepository

    private fun getTermsFromCache() {
        launch {
            view?.setError(null)
            view?.setLoading(true)
            tryIt({
                val terms = io {
                    commonEduRepository.getTermsFromCache()
                }
                view?.setTerms(terms)
                view?.setLoading(false)
            })
            getTermsFromApi()
        }
    }

    private fun getTermsFromApi() {
        launch {
            tryIt({
                val terms = io {
                    commonEduRepository.getTermsFromApi()
                }
                view?.setTerms(terms)
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setError(err)
            }
            view?.setLoading(false)
        }
    }

    override fun subscribe(view: ExamScoreActivityContract.View) {
        super.subscribe(view)
        getTermsFromCache()
    }
}