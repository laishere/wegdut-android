package com.wegdut.wegdut.ui.exam_score

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.util.keyIterator
import androidx.core.util.set
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.edu.Term
import com.wegdut.wegdut.data.edu.exam_score.ExamScore
import com.wegdut.wegdut.dialog.SimpleOptionListDialog
import com.wegdut.wegdut.utils.MessageUtils
import com.wegdut.wegdut.utils.UIUtils
import com.wegdut.wegdut.view.TabView
import dagger.android.support.DaggerAppCompatActivity
import java.math.BigDecimal
import java.text.DecimalFormat
import javax.inject.Inject

class ExamScoreActivity : DaggerAppCompatActivity(), ExamScoreActivityContract.View {
    private val adapter = ExamScoreFragmentAdapter(null, supportFragmentManager)
    private val optionListDialog = SimpleOptionListDialog(this, "选择学年")
    private val termMap = mutableMapOf<String, MutableList<Term>>()
    private val decimalFormat = DecimalFormat("0.##")
    private var oldTerms: List<Term>? = null
    private var oldActiveOption = -1

    @Inject
    lateinit var presenter: ExamScoreActivityContract.Presenter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tabView: TabView
    private lateinit var viewPager: ViewPager
    private lateinit var gradePointTextView: TextView
    private lateinit var termTextView: TextView
    private val scoresArray = SparseArray<Pair<BigDecimal, BigDecimal>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam_score)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 10
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener {
            supportFragmentManager.fragments.forEach {
                val f = it as ExamScoreFragment
                f.refresh()
            }
            swipeRefreshLayout.isRefreshing = false
        }
        UIUtils.setSwipeRefreshColor(swipeRefreshLayout)
        tabView = findViewById(R.id.tab_view)
        tabView.setupWithViewPager(viewPager)
        gradePointTextView = findViewById(R.id.grade_point)
        termTextView = findViewById(R.id.term)
        findViewById<View>(R.id.header_click_area).setOnClickListener {
            showOptionDialog()
        }
        optionListDialog.onOptionSelectedListener =
            object : SimpleOptionListDialog.OnOptionSelectedListener {
                override fun onSelected(position: Int) {
                    updateScores()
                    optionListDialog.dismiss()
                }
            }
        presenter.start()
        presenter.subscribe(this)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        presenter.stop()
        super.onDestroy()
    }

    fun setScores(term: Term, scores: List<ExamScore>) {
        val pos = adapter.terms!!.indexOf(term)
        var b = BigDecimal(0) // 学分 x 绩点 之和
        var credit = BigDecimal(0) // 学分之和
        for (s in scores) {
            if (s.gradePoint.isNotBlank() && s.credit.isNotBlank()) {
                val c = BigDecimal(s.credit)
                b += BigDecimal(s.gradePoint) * c
                credit += c
            }
        }
        scoresArray[pos] = b to credit
        applyGradePoints()
    }

    @SuppressLint("SetTextI18n")
    private fun applyGradePoints() {
        var sum = BigDecimal(0)
        var credit = BigDecimal(0)
        val gradePoints = SparseArray<String>()
        for (k in scoresArray.keyIterator()) {
            val s = scoresArray[k]
            sum = sum.add(s.first)
            credit += s.second
            gradePoints[k] =
                if (s.second > BigDecimal.ZERO)
                    decimalFormat.format(s.first / s.second)
                else ""
        }
        gradePointTextView.text =
            if (credit > BigDecimal.ZERO)
                decimalFormat.format(sum / credit)
            else "-.-"
        adapter.gradePoints = gradePoints
        tabView.notifyTitlesChanged()
    }

    private fun showOptionDialog() {
        if (optionListDialog.options.isNullOrEmpty()) {
            MessageUtils.info(this, "没有学期信息")
            return
        }
        optionListDialog.show()
    }

    override fun setLoading(loading: Boolean) {
        swipeRefreshLayout.isRefreshing = loading
    }

    override fun setTerms(terms: List<Term>) {
        if (oldTerms == terms) return
        oldTerms = terms
        oldActiveOption = -1
        termMap.clear()
        for (t in terms) {
            if (t.yearName !in termMap)
                termMap[t.yearName] = mutableListOf()
            termMap[t.yearName]!!.add(t)
        }
        val options = mutableListOf("所有学期")
        var active = 0
        val now = System.currentTimeMillis()
        for (i in termMap) {
            options.add(i.key)
            val t1 = i.value.last()
            val t2 = i.value.first()
            if (now in t1.start.time..t2.end.time)
                active = options.size - 1
        }
        optionListDialog.options = options
        optionListDialog.active = active
        updateScores()
    }

    private fun updateScores() {
        val active = optionListDialog.active
        if (oldActiveOption == active) return
        oldActiveOption = active
        val options = optionListDialog.options ?: return
        if (active !in options.indices) return
        val activeOption = options[active]
        termTextView.text = activeOption
        val terms = mutableListOf<Term>()
        if (active == 0) {
            for (i in termMap.values)
                terms.addAll(i)
        } else {
            terms.addAll(termMap[activeOption]!!)
        }
        applyTerms(terms)
    }

    private fun applyTerms(terms: List<Term>) {
        scoresArray.clear()
        adapter.terms = terms
        adapter.notifyDataSetChanged()
        tabView.notifyTitlesChanged()
        tabView.setCurrentItem(0)
        viewPager.setCurrentItem(0, false)
        applyGradePoints()
    }

    override fun setError(error: String?) {
        error?.let {
            MessageUtils.info(this, it)
        }
    }
}