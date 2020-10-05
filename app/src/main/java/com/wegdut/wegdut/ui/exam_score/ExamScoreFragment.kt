package com.wegdut.wegdut.ui.exam_score

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.edu.Term
import com.wegdut.wegdut.data.edu.exam_score.ExamScore
import com.wegdut.wegdut.ui.BaseDaggerFragment
import com.wegdut.wegdut.view.LoadingWrapper
import javax.inject.Inject

class ExamScoreFragment : BaseDaggerFragment(R.layout.fragment_exam_score),
    ExamScoreFragmentContract.View {

    @Inject
    lateinit var presenter: ExamScoreFragmentContract.Presenter
    private var termToGet: Term? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingWrapper: LoadingWrapper
    private val adapter = ExamScoreAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingWrapper = view.findViewById(R.id.loading_wrapper)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        presenter.subscribe(this)
        termToGet?.let { presenter.getExamScores(it.term) }
    }

    override fun onDestroyView() {
        presenter.unsubscribe()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.start()
    }

    override fun onDestroy() {
        presenter.stop()
        super.onDestroy()
    }

    fun setTerm(term: Term) {
        termToGet = term
    }

    fun refresh() {
        if (loadingWrapper.loading)
            return
        presenter.refresh()
    }

    override fun setExamScores(scores: List<ExamScore>) {
        loadingWrapper.empty = scores.isEmpty()
        adapter.diff(scores)
        activity?.let {
            val act = it as ExamScoreActivity
            act.setScores(termToGet!!, scores)
        }
    }

    override fun setLoading(loading: Boolean) {
        loadingWrapper.loading = loading
    }

    override fun setError(error: String?) {
        loadingWrapper.error = error
    }
}