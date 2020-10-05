package com.wegdut.wegdut.ui.exam_score

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.edu.exam_score.ExamScoreRepository
import javax.inject.Inject

class ExamScorePresenter @Inject constructor() : ExamScoreFragmentContract.Presenter() {

    @Inject
    lateinit var repository: ExamScoreRepository
    private var term: String? = null

    override fun getExamScores(term: String) {
        this.term = term
        jobs.clear()
        getExamScoresFromCache(term)
    }

    private fun getExamScoresFromCache(term: String) {
        launch {
            view?.setLoading(true)
            view?.setError(null)
            tryIt({
                val scores = io {
                    repository.getScoresFromCache(term)
                }
                view?.setExamScores(scores)
            })
            getExamScoresFromApi(term)
        }
    }

    private fun getExamScoresFromApi(term: String) {
        launch {
            tryIt({
                val scores = io {
                    repository.getScoresFromApi(term)
                }
                view?.setExamScores(scores)
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setError(err)
            }
            view?.setLoading(false)
        }
    }

    override fun refresh() {
        repository.refresh()
        term?.let { getExamScores(it) }
    }
}