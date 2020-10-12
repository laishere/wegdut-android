package com.wegdut.wegdut.ui.course_table

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.course_table.CourseTableData
import com.wegdut.wegdut.data.edu.Term
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.dialog.CourseDetailsDialog
import com.wegdut.wegdut.dialog.CourseTermOptionDialog
import com.wegdut.wegdut.helper.SimpleTimer
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.utils.DateUtils
import com.wegdut.wegdut.utils.MessageUtils
import com.wegdut.wegdut.utils.UIUtils
import com.wegdut.wegdut.view.CourseTable
import com.wegdut.wegdut.view.LoadingWrapper
import com.wegdut.wegdut.view.WeekTabs
import dagger.android.support.DaggerAppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class CourseTableActivity : DaggerAppCompatActivity(), CourseTableContract.View {
    @Inject
    lateinit var presenter: CourseTableContract.Presenter
    private lateinit var loadingWrapper: LoadingWrapper
    private lateinit var weekTabs: WeekTabs
    private lateinit var viewPager2: ViewPager2
    private val weekTabsTimer = SimpleTimer()
    private val adapter = CourseTableAdapter()
    private var courseTable: CourseTableData? = null
    private var terms: List<Term>? = null
    private var termsMap = mutableMapOf<Pair<Int, Int>, Term>()
    private lateinit var weekTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var termWrapper: View
    private lateinit var termTextView: TextView
    private val termOptionDialog = CourseTermOptionDialog(this)
    private val courseDetailsDialog = CourseDetailsDialog(this)
    private val dayViews = mutableListOf<DayViewHolder>()
    private var realActiveDay = -1
    private var currentWeek = 0
    private var activeDay = -1
        set(value) {
            if (field > -1) {
                dayViews[field].run {
                    wrapper.isSelected = false
                    weekday.typeface = Typeface.DEFAULT
                    date.typeface = Typeface.DEFAULT
                }
            }
            if (value > -1) {
                dayViews[value].run {
                    wrapper.isSelected = true
                    weekday.typeface = Typeface.DEFAULT_BOLD
                    date.typeface = Typeface.DEFAULT_BOLD
                }
            }
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        viewPager2 = findViewById(R.id.view_pager)
        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = 2
        viewPager2.orientation = ViewPager2.ORIENTATION_VERTICAL
        loadingWrapper = findViewById(R.id.loading_wrapper)
        weekTabs = findViewById(R.id.week_tabs)
        UIUtils.displayBackInToolbar(this, toolbar)
        weekTextView = findViewById(R.id.week)
        weekTextView.text = "-"
        errorTextView = findViewById(R.id.error)
        termWrapper = findViewById(R.id.term_wrapper)
        termTextView = findViewById(R.id.term)
        termWrapper.setOnClickListener { showTerms() }
        initCourseNum()
        initDays()
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                weekTextView.text = "${position + 1}"
                weekTabs.moveTo(position.toFloat(), true)
                activeDay = if (position + 1 == currentWeek) realActiveDay else -1
                updateCourseLabelAndDate()
            }
        })
        weekTabs.listener = object : WeekTabs.WeekTabListener {
            override fun onTabSelected(pos: Int) {
                viewPager2.setCurrentItem(pos, false)
            }

            override fun onTabScrolled(pos: Float) {}
        }
        adapter.onItemClickListener = object : ListRVAdapter.OnItemClickListener {
            override fun onClick(view: View, pos: Int) {
                weekTabsTimer.clear()
                weekTabs.toggle()
            }
        }
        adapter.onCourseItemClickListener = onCourseItemClickListener
        termOptionDialog.onTermSelectedListener =
            object : CourseTermOptionDialog.OnTermSelectedListener {
                override fun onTermSelected(yearIndex: Int, termIndex: Int) {
                    termsMap[yearIndex to termIndex]?.let {
                        presenter.getCourseData(it.term)
                        setCurrentTerm(it)
                    }
                    termOptionDialog.dismiss()
                }
            }
        presenter.start()
        presenter.subscribe(this)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        presenter.stop()
        courseDetailsDialog.dismiss()
        super.onDestroy()
    }

    private fun showTerms() {
        if (terms.isNullOrEmpty()) {
            MessageUtils.info(this, "没有学期信息")
            return
        }
        termOptionDialog.show()
    }

    private fun getCurrentOrNextTerm(termsDesc: List<Term>): Term? {
        var ans: Term? = null
        val now = System.currentTimeMillis()
        for (i in termsDesc) {
            when {
                now in i.start.time..i.end.time -> return i
                now < i.start.time -> ans = i
                else -> return ans
            }
        }
        return ans
    }

    private fun updateCourseLabelAndDate() {
        val i = viewPager2.currentItem
        val item = adapter.items?.get(i) ?: return
        val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        val firstMonday = DateUtils.mondayOfGivenDate(courseTable!!.startDate)
        val calendar = Calendar.getInstance()
        calendar.time = firstMonday
        calendar.add(Calendar.WEEK_OF_YEAR, i)
        for (v in dayViews) {
            v.label.visibility = View.GONE
            v.date.text = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        for (c in item) {
            val v = dayViews[c.weekDay - 1]
            val label = v.label
            val labelText = c.label
            if (labelText.isBlank())
                label.visibility = View.GONE
            else {
                label.visibility = View.VISIBLE
                label.isSelected = labelText.contains("补")
                label.isHovered = !label.isSelected
                label.text = labelText
            }
        }
    }

    private val onCourseItemClickListener = object : CourseTable.OnCourseItemClickListener {
        override fun onClick(course: List<Course>) {
            showDialog(course)
        }
    }

    private fun initCourseNum() {
        val courseNum: ViewGroup = findViewById(R.id.course_num)
        for (i in 1..12) {
            val tv = TextView(this)
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
            lp.weight = 1f
            tv.layoutParams = lp
            tv.gravity = Gravity.CENTER
            tv.textSize = 12f
            tv.text = "$i"
            courseNum.addView(tv)
        }
    }

    private fun initDays() {
        val days: ViewGroup = findViewById(R.id.days)
        val arr = "一二三四五六日"
        val inflater = LayoutInflater.from(this)
        for (i in arr.withIndex()) {
            val v = inflater.inflate(R.layout.item_course_day, days, false)
            val weekday = v.findViewById<TextView>(R.id.weekday)
            val date = v.findViewById<TextView>(R.id.date)
            val label = v.findViewById<TextView>(R.id.label)
            weekday.text = "${i.value}"
            days.addView(v)
            dayViews.add(DayViewHolder(v, weekday, date, label))
        }
    }

    private data class DayViewHolder(
        val wrapper: View,
        val weekday: TextView,
        val date: TextView,
        val label: TextView
    )

    @SuppressLint("SetTextI18n")
    private fun setCurrentTerm(term: Term) {
        for (i in termsMap) {
            if (i.value == term) {
                termOptionDialog.setActiveTerm(i.key.first, i.key.second)
                break
            }
        }
        termTextView.text = "${term.yearName} · ${term.termName}"
    }

    override fun setTerms(terms: List<Term>) {
        val sortedTerms = terms.sortedByDescending { it.end }
        if (this.terms == sortedTerms) return
        this.terms = sortedTerms
        var lastYear = ""
        var yearIndex = -1
        var termIndex = 0
        val itemsMap = mutableMapOf<String, MutableList<String>>()
        for (i in terms) {
            if (lastYear != i.yearName) {
                yearIndex++
                termIndex = 0
                lastYear = i.yearName
                itemsMap[i.yearName] = mutableListOf()
            }
            termsMap[yearIndex to termIndex] = i
            itemsMap[i.yearName]!!.add(i.termName)
            termIndex++
        }
        val items = itemsMap.map { CourseTermOptionDialog.CourseTerm(it.key, it.value) }
        termOptionDialog.setItems(items)
        getCurrentOrNextTerm(terms)?.let {
            presenter.getCourseData(it.term)
            setCurrentTerm(it)
        }
    }

    override fun setTermLoading(loading: Boolean) {
        if (courseTable == null && loading)
            setCourseLoading(loading)
    }

    override fun setTermLoadingError(error: String?) {
        if (error != null)
            MessageUtils.info(this, "更新学期信息失败：$error")
    }

    override fun setCourseLoading(loading: Boolean) {
        loadingWrapper.loading = loading
    }

    override fun setCourseData(data: CourseTableData) {
        if (courseTable == data) return
        courseTable = data
        adapter.items = data.course
        adapter.notifyDataSetChanged()
        val now = Date()
        currentWeek = DateUtils.relativeWeekOfDate(now) -
                DateUtils.relativeWeekOfDate(data.startDate) + 1
        val calendar = Calendar.getInstance()
        calendar.time = now
        var d = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY
        d = (d + 7) % 7
        realActiveDay = d
        val index = max(1, min(data.course.size, currentWeek)) - 1
        viewPager2.setCurrentItem(index, false)
        weekTabs.weekStart = 1
        weekTabs.weekEnd = data.course.size
        weekTabsTimer.clear()
        weekTabs.show()
        weekTabsTimer.delay(3000L) {
            weekTabs.hide()
        }
    }

    override fun setCourseLoadingError(error: String?) {
        when {
            error == null -> {
                viewPager2.visibility = View.VISIBLE
                errorTextView.visibility = View.GONE
            }
            courseTable != null -> {
                MessageUtils.info(this, "更新课程信息失败：$error")
                viewPager2.visibility = View.VISIBLE
                errorTextView.visibility = View.GONE
            }
            else -> {
                viewPager2.visibility = View.GONE
                weekTabs.hide()
                errorTextView.text = error
                errorTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun showDialog(courses: List<Course>) {
        courseDetailsDialog.show(courses)
    }
}