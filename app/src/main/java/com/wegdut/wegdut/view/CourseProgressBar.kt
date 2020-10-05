package com.wegdut.wegdut.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.wegdut.wegdut.R
import kotlin.math.max

class CourseProgressBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint: Paint = Paint()
    var progress = 0f
        set(value) {
            field = value
            updateActiveBarHeight()
            invalidate()
        }
    var progressPoints = 0
        set(value) {
            field = value
            updateBarHeight()
            updateActiveBarHeight()
            invalidate()
        }
    private var headHeight = 0
    private var footerHeight = 0
    private var itemHeight = 0
    private var barRect = RectF()
    private var activeBarRect = RectF()
    private var color = 0
    private var activeColor = 0

    init {
        paint.isAntiAlias = true
        val ta = context.obtainStyledAttributes(attrs, R.styleable.CourseProgressBar)
        headHeight = ta.getDimensionPixelSize(R.styleable.CourseProgressBar_headHeight, 0)
        itemHeight = ta.getDimensionPixelSize(R.styleable.CourseProgressBar_itemHeight, 0)
        footerHeight = ta.getDimensionPixelSize(R.styleable.CourseProgressBar_footerHeight, 0)
        color = ta.getColor(R.styleable.CourseProgressBar_color, color)
        activeColor = ta.getColor(R.styleable.CourseProgressBar_activeColor, activeColor)
        ta.recycle()
    }

    private fun updateBarHeight() {
        barRect.top = 0f
        barRect.bottom = (headHeight + itemHeight * (progressPoints - 1) + footerHeight).toFloat()
    }

    private fun updateActiveBarHeight() {
        activeBarRect.top = 0f
        if (progress <= 1f) {
            activeBarRect.bottom = progress * headHeight
            return
        }
        val p = progress - 1f
        activeBarRect.bottom = p * itemHeight + headHeight
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        paint.color = color
        val r = width / 2f
        barRect.right = width.toFloat()
        activeBarRect.right = width.toFloat()
        canvas.drawRoundRect(barRect, r, r, paint)
        paint.color = activeColor
        canvas.drawRoundRect(activeBarRect, r, r, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, suggestedMinimumHeight)
    }

    override fun getSuggestedMinimumHeight(): Int {
        return max(0, barRect.height().toInt())
    }
}