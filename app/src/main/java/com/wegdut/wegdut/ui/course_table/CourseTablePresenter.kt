package com.wegdut.wegdut.ui.course_table

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.course_table.CourseTableRepository
import com.wegdut.wegdut.ui.Jobs
import javax.inject.Inject

class CourseTablePresenter @Inject constructor() : CourseTableContract.Presenter() {
    @Inject
    lateinit var repository: CourseTableRepository
    private val courseJobs = Jobs()

    override fun getCourseData(term: String) {
        courseJobs.clear()
        getCourseFromCache(term)
    }

    override fun subscribe(view: CourseTableContract.View) {
        super.subscribe(view)
        if (isResubscribe) getTermFromApi()
        else getTermFromCache()
    }

    private fun getCourseFromCache(term: String) {
        view?.setCourseLoadingError(null)
        view?.setCourseLoading(true)
        val job = launch {
            tryIt({
                val data = io {
                    repository.getCourseFromCache(term)
                }
                view?.setCourseData(data)
                view?.setCourseLoading(false)
            })
            getCourseFromApi(term)
        }
        courseJobs.add(job)
    }

    private fun getCourseFromApi(term: String) {
        val job = launch {
            tryIt({
                val data = io {
                    repository.getCourseFromApi(term)
                }
                view?.setCourseData(data)
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it, "无法获取课表信息")
                view?.setCourseLoadingError(err)
            }
            view?.setCourseLoading(false)
        }
        courseJobs.add(job)
    }

    private fun getTermFromCache() {
        view?.setTermLoadingError(null)
        view?.setTermLoading(true)
        launch {
            tryIt({
                val terms = io {
                    repository.getTermsFromCache()
                }
                view?.setTerms(terms)
                view?.setTermLoading(false)
            })
            getTermFromApi()
        }
    }

    private fun getTermFromApi() {
        launch {
            tryIt({
                val terms = io {
                    repository.getTermsFromApi()
                }
                view?.setTerms(terms)
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setTermLoadingError(err)
            }
            view?.setTermLoading(false)
        }
    }
}