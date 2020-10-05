package com.wegdut.wegdut.ui.user


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.SupportMenuInflater
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wegdut.wegdut.MyApplication
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.edu.exam_plan.ExamPlan
import com.wegdut.wegdut.drawable.VerticalDashedLine
import com.wegdut.wegdut.ui.BaseDaggerFragment
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.ui.exam_score.ExamScoreActivity
import com.wegdut.wegdut.ui.login.LoginActivity
import com.wegdut.wegdut.ui.setting.SettingActivity
import com.wegdut.wegdut.utils.DateUtils
import com.wegdut.wegdut.utils.GlideUtils
import com.wegdut.wegdut.utils.MessageUtils
import com.wegdut.wegdut.utils.UIUtils
import com.wegdut.wegdut.view.CourseProgressBar
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KClass

class UserFragment : BaseDaggerFragment(R.layout.fragment_user), UserContract.View {

    @Inject
    lateinit var presenter: UserContract.Presenter

    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var examPlanWrapper: ViewGroup
    private lateinit var examPlanRecyclerView: RecyclerView
    private lateinit var icon: ImageView
    private lateinit var nickname: TextView
    private lateinit var progressBar: CourseProgressBar
    private val menuAdapter = GridMenuAdapter()
    private val examPlanAdapter = ExamPlanAdapter()
    private lateinit var examDialog: Dialog
    private lateinit var examDialogViewHolder: ExamDialogViewHolder
    private lateinit var examPlanHint: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UIUtils.addStatusBarPadding(view)
        initView(view)
        setupMenu()
        setupExamPlan()
        setupUser()
        presenter.subscribe(this)
    }

    override fun onDestroyView() {
        presenter.unsubscribe()
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        updateUser()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.start()
    }

    override fun onDestroy() {
        presenter.stop()
        super.onDestroy()
    }

    private fun initView(v: View) {
        menuRecyclerView = v.findViewById(R.id.menu)
        examPlanWrapper = v.findViewById(R.id.exam_plan_wrapper)
        examPlanRecyclerView = v.findViewById(R.id.exam_plan_recycler_view)
        examPlanHint = v.findViewById(R.id.exam_hint)
        icon = v.findViewById(R.id.icon)
        nickname = v.findViewById(R.id.nickname)
        progressBar = v.findViewById(R.id.progress_bar)
        val settingBtn: View = v.findViewById(R.id.btn_setting)
        settingBtn.setOnClickListener { startSetting() }
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener { presenter.refresh() }
        UIUtils.setSwipeRefreshColor(swipeRefreshLayout)
    }

    private fun startSetting() {
        activity?.let {
            val intent = Intent(it, SettingActivity::class.java)
            it.startActivity(intent)
        }
    }

    private fun setupUser() {
        nickname.setOnClickListener {
            val u = MyApplication.user
            if (u == null)
                startLogin()
        }
    }

    private fun startLogin() {
        activity?.let {
            val intent = Intent(it, LoginActivity::class.java)
            it.startActivity(intent)
        }
    }

    override fun updateUser() {
        val u = MyApplication.user
        if (u == null) {
            icon.setImageResource(R.drawable.ic_default_user_icon)
            nickname.text = "登录 / 注册"
        } else {
            GlideUtils.loadIcon(icon, u.icon)
            nickname.text = u.nickname
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setupMenu() {
        val ctx = menuRecyclerView.context
        menuRecyclerView.layoutManager = GridLayoutManager(ctx, 5)
        menuRecyclerView.adapter = menuAdapter
        val menu = MenuBuilder(ctx)
        SupportMenuInflater(ctx).inflate(R.menu.user_menu, menu)
        menuAdapter.items = menu.children.toList()
        menuAdapter.onItemClickListener = onGridMenuClickListener
        menuRecyclerView.suppressLayout(true)
    }

    private val onGridMenuClickListener = object : ListRVAdapter.OnItemClickListener {
        override fun onClick(view: View, pos: Int) {
            when (menuAdapter.items!![pos].itemId) {
                R.id.score -> start(ExamScoreActivity::class)
                else -> MessageUtils.info(context, "即将上线")
            }
        }
    }

    private fun start(cls: KClass<*>) {
        val intent = Intent(context, cls.java)
        startActivity(intent)
    }

    private fun setupExamPlan() {
        examPlanWrapper.visibility = View.GONE
        examPlanRecyclerView.adapter = examPlanAdapter
        examPlanRecyclerView.layoutManager = LinearLayoutManager(context)
        examPlanAdapter.onItemClickListener = onExamClickListener
    }

    private val onExamClickListener = object : ListRVAdapter.OnItemClickListener {
        override fun onClick(view: View, pos: Int) {
            showExamDialog(examPlanAdapter.items?.get(pos)!!)
        }
    }

    override fun setExamPlan(items: List<ExamPlan>) {
        if (items.isEmpty()) {
            examPlanWrapper.visibility = View.GONE
            return
        }
        examPlanWrapper.visibility = View.VISIBLE
        examPlanRecyclerView.suppressLayout(false)
        examPlanAdapter.items = items
        examPlanAdapter.notifyDataSetChanged()
        examPlanRecyclerView.suppressLayout(true)
        progressBar.progressPoints = items.size
        progressBar.progress = calculateExamPlanProgress(items)
        val hint = calculateExamHint(items)
        if (hint == null) examPlanHint.visibility = View.GONE
        else {
            examPlanHint.visibility = View.VISIBLE
            examPlanHint.text = hint
        }
    }

    override fun setExamPlanError(error: String?) {
        if (error == null) return
        MessageUtils.info(context, "获取考试安排：$error")
    }

    override fun setRefreshing(refreshing: Boolean) {
        swipeRefreshLayout.isRefreshing = refreshing
    }

    private fun calculateExamHint(items: List<ExamPlan>): String? {
        val now = Date()
        for (i in items) {
            if (i.end.after(now)) {
                val delta = DateUtils.deltaDay(i.start, now)
                if (delta > 0)
                    return "离最近的考试仅剩${delta}天"
                return "今天有考试，祝君凯旋"
            }
        }
        return null
    }

    private fun calculateExamPlanProgress(items: List<ExamPlan>): Float {
        val now = System.currentTimeMillis()
        var p = 0f
        var last: ExamPlan? = null
        for (i in items) {
            if (now < i.start.time) {
                last?.let {
                    val lastEnd = it.end.time
                    val start = i.start.time
                    p += (now - lastEnd).toFloat() / (start - lastEnd)
                }
                break
            }
            p += 1f
            if (now <= i.end.time) break
            last = i
        }
        return p
    }

    private fun showExamDialog(item: ExamPlan) {
        if (!this::examDialog.isInitialized) {
            examDialog = Dialog(context!!, R.style.SimpleDialog)
            examDialog.setContentView(R.layout.dialog_exam_plan)
            examDialog.show()
            val rightCard: View = examDialog.findViewById(R.id.right_card)
            rightCard.background = VerticalDashedLine(context!!, R.color.grey_300, 1f, 5f, 5f)
            examDialogViewHolder = ExamDialogViewHolder(examDialog)
            examDialogViewHolder.bind(item)
            return
        }
        examDialogViewHolder.bind(item)
        examDialog.show()
    }

    class ExamDialogViewHolder(dialog: Dialog) {
        private val subject: TextView = dialog.findViewById(R.id.subject)
        private val type: TextView = dialog.findViewById(R.id.type)
        private val time: TextView = dialog.findViewById(R.id.time)
        private val classroom: TextView = dialog.findViewById(R.id.classroom)
        private val teacher: TextView = dialog.findViewById(R.id.teacher)
        private val number: TextView = dialog.findViewById(R.id.number)
        private val student: TextView = dialog.findViewById(R.id.student)

        @SuppressLint("SetTextI18n")
        fun bind(item: ExamPlan) {
            subject.text = item.subject
            type.text = "${item.examType} · ${item.planType}"
            val date = DateUtils.format(item.start, DateUtils.FormatType.SHORT_DATE)
            val startTime = DateUtils.format(item.start, DateUtils.FormatType.TIME_TO_MINUTE)
            val endTime = DateUtils.format(item.end, DateUtils.FormatType.TIME_TO_MINUTE)
            time.text = "$date $startTime~$endTime"
            classroom.text = item.classroom
            teacher.text = if (item.teacher.isBlank()) "[空]" else item.teacher
            number.text = item.number
            student.text = item.name
        }
    }

}