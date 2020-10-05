package com.wegdut.wegdut.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.Keep
import androidx.core.animation.doOnEnd
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.edu.course.CourseStatus
import com.wegdut.wegdut.data.edu.course.DayCourse
import com.wegdut.wegdut.ui.home.HomeAdapter
import com.wegdut.wegdut.utils.CourseUtils
import com.wegdut.wegdut.utils.DateUtils
import com.wegdut.wegdut.utils.MessageUtils
import java.util.*
import kotlin.math.roundToInt

class CourseWrapper(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var position = 0f
    private val todayHead: View
    private val tomorrowHead: View
    private val todayProgressBar: CourseProgressBar
    private val tomorrowProgressBar: CourseProgressBar
    private val todayCourse: View = LayoutInflater.from(context).inflate(
        R.layout.item_home_today_course, this, false
    )
    private val tomorrowCourse: View = LayoutInflater.from(context).inflate(
        R.layout.item_home_tomorrow_course, this, false
    )
    private val todayHeadText: TextView = todayCourse.findViewById(R.id.head_text)
    private val todayCourseWrapper: ViewGroup = todayCourse.findViewById(R.id.course_wrapper)
    private val todayCourseError: TextView = todayCourse.findViewById(R.id.error)
    private val todayCourseLoading: TextView = todayCourse.findViewById(R.id.loading)
    private val todayCourseNoCourse: TextView = todayCourse.findViewById(R.id.no_course)
    private val tomorrowCourseWrapper: ViewGroup = tomorrowCourse.findViewById(R.id.course_wrapper)
    private val tomorrowHeadText: TextView = tomorrowCourse.findViewById(R.id.head_text)
    private val objectAnimator = ObjectAnimator
        .ofFloat(this, "animationValue", 0f, 1f)
    private var canCollapse = true
    var homeCourseData: HomeAdapter.HomeCourseData? = null
        set(value) {
            field = value
            onUpdate()
        }

    @SuppressLint("SetTextI18n")
    private fun onUpdate() {
        homeCourseData?.let {
            onLoading()
            val error = it.updateError
            todayCourseError.visibility = View.GONE
            todayCourseNoCourse.visibility = View.GONE
            tomorrowCourse.visibility = View.GONE
            if (error != null) onError()
            it.course?.let { homeCourse ->
                todayHeadText.text = homeCourse.weekAndDay
                if (homeCourse.isTermCourseEnd) {
                    var desc = "本学期课程已结束"
                    if (homeCourse.hasNextTermCourse)
                        desc += "\n可在课程表中查看下学期课程"
                    onTermEnd(desc)
                    return
                }
                tomorrowCourse.visibility = View.VISIBLE
                if (homeCourse.tomorrowCourse.noCourseDesc.isBlank())
                    tomorrowHeadText.text = getCourseAbstract(homeCourse.tomorrowCourse)
                else updateTomorrowNoCourse(homeCourse.tomorrowCourse)
                if (homeCourse.todayCourse.noCourseDesc.isNotBlank())
                    updateTodayNoCourse(homeCourse.todayCourse)
            }
            if (it.course == null) {
                todayHeadText.text = "星期${DateUtils.weekDayInChinese(Date())}"
            }
            updateCourse(todayCourseWrapper, todayProgressBar, it.course?.todayCourse)
            updateCourse(tomorrowCourseWrapper, tomorrowProgressBar, it.course?.tomorrowCourse)
        }
        updateInteraction()
    }

    @SuppressLint("SetTextI18n")
    private fun onTermEnd(desc: String) {
        todayCourseWrapper.visibility = View.GONE
        todayCourseNoCourse.text = desc
        todayCourseNoCourse.visibility = View.VISIBLE
        updateInteraction()
    }

    private fun updateInteraction() {
        // 如果明日课程卡片隐藏或者无课...
        if (tomorrowCourse.visibility == View.GONE || tomorrowCourseWrapper.visibility == View.GONE) {
            objectAnimator.cancel()
            canCollapse = false
            if (position != 0f) setAnimationValue(0f)
        } else {
            canCollapse = true
        }
    }

    private fun onError() {
        val error = homeCourseData!!.updateError
        if (homeCourseData!!.course == null) {
            todayCourseError.text = error
            todayCourseError.visibility = VISIBLE
        } else {
            MessageUtils.info(context, "课表更新失败\n$error")
        }
    }

    private fun onLoading() {
        val loading = homeCourseData!!.loading
        todayCourseLoading.visibility =
            if (loading && homeCourseData!!.course == null) View.VISIBLE else View.GONE
    }

    private fun updateTodayNoCourse(course: DayCourse) {
        todayCourseNoCourse.text = course.noCourseDesc
        todayCourseNoCourse.visibility = View.VISIBLE
    }

    private fun updateTomorrowNoCourse(course: DayCourse) {
        tomorrowHeadText.text = course.noCourseDesc
    }

    private fun getCourseAbstract(course: DayCourse): String {
        val list = course.course
        if (list.isEmpty()) return "全天无课"
        val am = list.sumBy {
            if (it.from < 5) 1
            else 0
        }
        val pm = list.size - am
        val num = { n: Int ->
            when (n) {
                1 -> "一节"
                2 -> "两节"
                3 -> "三节"
                4 -> "四节"
                else -> "无课"
            }
        }
        return "上午${num(am)}，下午${num(pm)}"
    }

    private fun updateCourse(
        wrapper: ViewGroup,
        progressBar: CourseProgressBar,
        course: DayCourse?
    ) {
        if (course == null || course.course.isEmpty()) {
            wrapper.visibility = View.GONE
            progressBar.visibility = View.GONE
            return
        }
        wrapper.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        wrapper.removeAllViews()
        progressBar.progressPoints = course.course.size
        if (progressBar == todayProgressBar)
            progressBar.progress = calculateCourseProgress(course)
        for (c in course.course) {
            val item =
                LayoutInflater.from(context).inflate(R.layout.item_home_course_item, wrapper, false)
            val label = item.findViewById<View>(R.id.label)
            val status = CourseUtils.status(c)
            if (status == CourseStatus.FINISHED) label.isSelected = true
            else if (status == CourseStatus.RUNNING) label.isHovered = true
            val ids = arrayOf(R.id.name, R.id.location, R.id.time)
            val text = arrayOf(c.name, c.location, CourseUtils.getTime(c))
            for (i in ids.zip(text))
                item.findViewById<TextView>(i.first).text = i.second
            wrapper.addView(item)
        }
    }

    private fun calculateCourseProgress(course: DayCourse): Float {
        var lastTime = 0
        var progress = 0f
        val now = DateUtils.getTimeInSecond(Date())
        for (c in course.course) {
            val t = CourseUtils.getTimeInSecond(c)
            if (now > t.first) progress += 1f
            else {
                progress += (now - lastTime).toFloat() / (t.first - lastTime)
                break
            }
            if (now <= t.second) break
            lastTime = t.second
        }
        return progress
    }

    init {
        todayHead = todayCourse.findViewById(R.id.head)
        todayProgressBar = todayCourse.findViewById(R.id.progress_bar)

        tomorrowHead = tomorrowCourse.findViewById(R.id.head)
        tomorrowProgressBar = tomorrowCourse.findViewById(R.id.progress_bar)

        addView(todayCourse)
        addView(tomorrowCourse)

        objectAnimator.duration = 200L
        objectAnimator.doOnEnd {
            setLayerType(View.LAYER_TYPE_NONE, null)
        }

        tomorrowHead.setOnClickListener {
            MyLog.debug(this, "${tomorrowCourseWrapper.height}")
            if (!canCollapse) return@setOnClickListener
            if (objectAnimator.isRunning) return@setOnClickListener
            val arr = arrayOf(0f, 1f)
            if (position > 0f) arr.reverse()
            objectAnimator.setFloatValues(arr[0], arr[1])
            objectAnimator.start()
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }
    }

    @Keep
    private fun setAnimationValue(t: Float) {
        position = t
        requestLayout()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        updateViewPosition()
    }

    private fun updateViewPosition() {
        val y1 = todayHead.measuredHeight
        val y2 = todayCourse.measuredHeight
        tomorrowCourse.translationY = y2 + (y1 - y2) * position
        tomorrowProgressBar.alpha = position
        todayProgressBar.alpha = 1 - position
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, calculateHeight())
    }

    private fun calculateHeight(): Int {
        // 明日课程折叠
        val tomorrowHeadHeight: Int
        val tomorrowCourseHeight: Int
        if (tomorrowCourse.visibility == View.VISIBLE) {
            tomorrowCourseHeight = tomorrowCourse.measuredHeight
            tomorrowHeadHeight = tomorrowHead.measuredHeight
        } else {
            tomorrowCourseHeight = 0
            tomorrowHeadHeight = 0
        }
        val h1 = todayCourse.measuredHeight + tomorrowHeadHeight
        // 今日课程折叠
        val h2 = todayHead.measuredHeight + tomorrowCourseHeight
        return (h1 + (h2 - h1) * position).roundToInt()
    }
}