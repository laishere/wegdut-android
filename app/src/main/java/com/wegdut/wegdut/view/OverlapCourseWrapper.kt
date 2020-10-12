package com.wegdut.wegdut.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.getOrDefault
import androidx.core.util.set
import androidx.core.view.setPadding
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.edu.course.Course
import com.wegdut.wegdut.data.edu.course.CourseStatus
import com.wegdut.wegdut.utils.CourseUtils
import com.wegdut.wegdut.utils.UIUtils
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * 支持重叠课程的显示
 */
class OverlapCourseWrapper(context: Context) : ViewGroup(context) {

    private var courses: List<Course>? = null
    private val courseColumnInfo = mutableListOf<Int>()
    private val courseCountInfo = SparseArray<Int>()
    var from = 0
        private set
    var to = 0
        private set

    companion object {
        const val PADDING_FACTOR = 0.02f
    }

    fun setCourses(courses: List<Course>?) {
        this.courses = courses
        if (courses.isNullOrEmpty())
            visibility = GONE
        else {
            // 先开始的课程在下面， 同时开始的先结束的在下面
            this.courses = courses.sortedWith(Comparator { a, b ->
                if (a.from != b.from) a.from - b.from
                else a.to - b.to
            })
            courseCountInfo.clear()
            removeAllViews()
            addViewForCourses()
            visibility = VISIBLE
            requestLayout()
        }
    }

    private fun addViewForCourses() {
        val layoutInflater = LayoutInflater.from(context)
        from = Int.MAX_VALUE
        to = Int.MIN_VALUE
        courseColumnInfo.clear()
        val strokeWidth = UIUtils.dp2px(context, 2f)
        val hsv = FloatArray(3)
        for (c in courses!!) {
            val view = layoutInflater.inflate(R.layout.item_course_table_text_view, this, false)
            view.findViewById<TextView>(R.id.name).text = c.name
            view.findViewById<TextView>(R.id.location).text = c.location
            val bg = view.background as GradientDrawable
            bg.mutate()
            val color =
                if (CourseUtils.status(c) == CourseStatus.FINISHED)
                    ResourcesCompat.getColor(
                        resources,
                        R.color.course_table_item_inactive,
                        null
                    )
                else CourseUtils.getColor(c.name)
            bg.setColor(color)

            if (courseColumnInfo.isNotEmpty()) {
                Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv)
                hsv[2] *= 0.6f
                bg.setStroke(strokeWidth, Color.HSVToColor(hsv))
            }

            addView(view)
            val column = courseCountInfo.getOrDefault(c.from, 0)
            courseCountInfo[c.from] = column + 1
            courseColumnInfo.add(column)
            from = min(from, c.from)
            to = max(to, c.to)
        }
    }

    private fun getPadding() = (measuredWidth * PADDING_FACTOR).roundToInt()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val courses = this.courses ?: return
        val padding = getPadding()
        val visibleWidth = measuredWidth - padding * 2
        val visibleHeight = measuredHeight - padding * 2
        val heightFactor = visibleHeight / (to + 1f - from)
        for (i in courses.withIndex()) {
            val c = i.value
            val l = padding + visibleWidth * courseColumnInfo[i.index] / courseCountInfo[c.from]
            val t = (padding + heightFactor * (c.from - from)).roundToInt()
            val child = getChildAt(i.index)
            child.layout(l, t, l + child.measuredWidth, t + child.measuredHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getDefaultSize(0, widthMeasureSpec),
            getDefaultSize(0, heightMeasureSpec)
        )
        val courses = this.courses ?: return
        val padding = getPadding()
        setPadding(padding)
        val width = measuredWidth - padding * 2
        val visibleHeight = (measuredHeight - padding * 2)
        val heightFactor = visibleHeight / (to + 1f - from)
        for (i in courses.withIndex()) {
            val c = i.value
            val h = (c.to + 1f - c.from) * heightFactor
            val child = getChildAt(i.index)
            child.measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(h.toInt(), MeasureSpec.EXACTLY)
            )
        }
    }

}