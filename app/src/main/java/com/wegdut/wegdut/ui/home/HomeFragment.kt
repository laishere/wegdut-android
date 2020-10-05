package com.wegdut.wegdut.ui.home


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.home.course.HomeCourse
import com.wegdut.wegdut.data.news.News
import com.wegdut.wegdut.helper.LazyUpdateHelper
import com.wegdut.wegdut.ui.BaseDaggerFragment
import com.wegdut.wegdut.ui.BaseTypeRVAdapter
import com.wegdut.wegdut.ui.Reselectable
import com.wegdut.wegdut.ui.course_table.CourseTableActivity
import com.wegdut.wegdut.ui.news.NewsActivity
import com.wegdut.wegdut.ui.news_details.NewsDetailsActivity
import com.wegdut.wegdut.utils.MessageUtils
import com.wegdut.wegdut.utils.UIUtils
import javax.inject.Inject

class HomeFragment : BaseDaggerFragment(R.layout.fragment_home), HomeContract.View, Reselectable {
    @Inject
    lateinit var presenter: HomeContract.Presenter
    private lateinit var recyclerView: RecyclerView
    private val adapter = HomeAdapter()
    private var homeCourse: HomeCourse? = null
    private var courseUpdating: Boolean = false
    private var courseLoading: Boolean = false
    private var courseUpdateError: String? = null
    private var news: List<News>? = null
    private var newsUpdating: Boolean = false
    private var newsLoading: Boolean = false
    private var newsUpdateError: String? = null
    private val lazyUpdateHelper =
        LazyUpdateHelper { notifyDataChanged() }
    private var refreshingCnt = 0
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var sp: SharedPreferences
    private var onlyForStudent = false

    companion object {
        const val ONLY_FOR_STUDENT_KEY = "ONLY_FOR_STUDENT"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UIUtils.addStatusBarPadding(view)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh)
        appBarLayout = view.findViewById(R.id.app_bar_layout)
        UIUtils.setSwipeRefreshColor(swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            refreshingCnt = 2
            presenter.refresh()
        }
        val searchBtn: View = view.findViewById(R.id.btn_search)
        searchBtn.setOnClickListener {
            MessageUtils.info(context, "即将上线")
        }
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = null
        adapter.newsHeadListener = newsHeadListener
        adapter.courseListener = courseListener
        adapter.onNewsItemClickListener = onNewsItemClickListener
        sp = context!!.getSharedPreferences(context!!.packageName, Context.MODE_PRIVATE)
        loadOnlyForStudentSetting()
        presenter.subscribe(this)
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

    private fun loadOnlyForStudentSetting() {
        onlyForStudent = sp.getBoolean(ONLY_FOR_STUDENT_KEY, false)
        presenter.setOnlyForStudent(onlyForStudent)
    }

    private fun updateOnlyForStudentSetting(only: Boolean) {
        sp.edit().putBoolean(ONLY_FOR_STUDENT_KEY, only).apply()
        onlyForStudent = only
    }

    private val courseListener = object : HomeAdapter.CourseListener {
        override fun onClick() {
            startActivity(CourseTableActivity::class.java)
        }
    }

    private val newsHeadListener = object : HomeAdapter.NewsHeadListener {
        override fun onNewsBtnClick() {
            startActivity(NewsActivity::class.java)
        }

        override fun onCheckedChange(isChecked: Boolean) {
            updateOnlyForStudentSetting(isChecked)
            presenter.setOnlyForStudent(isChecked)
            presenter.refreshNews()
        }
    }

    private val onNewsItemClickListener = object : HomeAdapter.OnNewsItemClickListener {
        override fun onClick(news: News) {
            startNewsDetails(news)
        }
    }

    private fun startNewsDetails(news: News) {
        val intent = Intent(context, NewsDetailsActivity::class.java)
        intent.putExtra(NewsDetailsActivity.ID, news.id)
        intent.putExtra(NewsDetailsActivity.TITLE, news.category)
        activity?.startActivity(intent)
    }

    private fun startActivity(cls: Class<*>) {
        val intent = Intent(activity, cls)
        activity?.startActivity(intent)
    }

    private fun notifyDataChanged() {
        val items = merge()
        adapter.diff(items)
    }

    private fun merge(): List<BaseTypeRVAdapter.Item> {
        val items = mutableListOf<BaseTypeRVAdapter.Item>()
        items.add(
            BaseTypeRVAdapter.Item(
                items.size.toLong(),
                R.layout.item_home_small_divider,
                null
            )
        )
        addCourse(items)
        addDivider(items)
        addNews(items)
        addDivider(items)
        return items
    }

    private fun addCourse(items: MutableList<BaseTypeRVAdapter.Item>) {
        items.add(
            BaseTypeRVAdapter.Item(
                1, R.layout.item_home_course_wrapper,
                HomeAdapter.HomeCourseData(
                    courseUpdating,
                    courseLoading,
                    courseUpdateError,
                    homeCourse
                )
            )
        )
    }

    private fun addNews(items: MutableList<BaseTypeRVAdapter.Item>) {
        items.add(
            BaseTypeRVAdapter.Item(
                1, R.layout.item_home_news_head, onlyForStudent
            )
        )
        val error = newsUpdateError
        if (error != null && !newsLoading) {
            if (news == null)
                items.add(
                    BaseTypeRVAdapter.Item(
                        1, R.layout.item_home_news_error, error
                    )
                )
            else MessageUtils.info(context, "校内通知\n$error")
        }
        if (news == null && newsLoading)
            items.add(
                BaseTypeRVAdapter.Item(1, R.layout.item_home_news_loading, null)
            )
        news?.let {
            for (i in it)
                items.add(
                    BaseTypeRVAdapter.Item(i.id, R.layout.item_home_news, i)
                )
        }
        items.add(
            BaseTypeRVAdapter.Item(
                1, R.layout.item_home_news_footer, null
            )
        )
    }

    private fun addDivider(items: MutableList<BaseTypeRVAdapter.Item>) {
        items.add(BaseTypeRVAdapter.Item(items.size.toLong(), R.layout.item_home_divider, null))
    }

    private fun lazyUpdate() {
        lazyUpdateHelper.update()
    }


    override fun setCourse(updated: Boolean, course: HomeCourse) {
        homeCourse = course
        lazyUpdate()
    }

    override fun setCourseUpdating(updating: Boolean) {
        courseUpdating = updating
        lazyUpdate()
    }

    override fun setCourseLoading(loading: Boolean) {
        courseLoading = loading
        lazyUpdate()
    }

    private fun onRefreshingChanged(refreshing: Boolean) {
        if (!refreshing) refreshingCnt--
        if (refreshingCnt == 0)
            swipeRefreshLayout.isRefreshing = false
    }

    override fun setCourseRefreshing(refreshing: Boolean) {
        onRefreshingChanged(refreshing)
    }

    override fun setCourseUpdateError(error: String?) {
        courseUpdateError = error
        lazyUpdate()
    }

    override fun setNews(news: List<News>) {
        this.news = news
        lazyUpdate()
    }

    override fun setNewsUpdating(updating: Boolean) {
        newsUpdating = updating
        lazyUpdate()
    }

    override fun setNewsLoading(loading: Boolean) {
        newsLoading = loading
        lazyUpdate()
    }

    override fun setNewsRefreshing(refreshing: Boolean) {
        onRefreshingChanged(refreshing)
    }


    override fun setNewsUpdateError(error: String?) {
        newsUpdateError = error
        lazyUpdate()
    }

    override fun reselect() {
        recyclerView.scrollToPosition(0)
        appBarLayout.setExpanded(true)
    }
}