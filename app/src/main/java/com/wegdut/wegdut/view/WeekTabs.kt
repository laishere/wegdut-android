package com.wegdut.wegdut.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.Keep
import com.wegdut.wegdut.R
import com.wegdut.wegdut.utils.UIUtils
import kotlin.math.absoluteValue
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class WeekTabs : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.WeekTabs)
        maxWidth = ta.getDimensionPixelSize(R.styleable.WeekTabs_maxWidth, maxWidth)
        ta.recycle()
    }

    var weekStart = 1
        set(value) {
            if (value == field) return
            field = value
            weekCnt = weekEnd - weekStart + 1
            requestLayout()
        }
    var weekEnd = 0
        set(value) {
            if (value == field) return
            field = value
            weekCnt = weekEnd - weekStart + 1
            requestLayout()
        }
    private var maxWidth: Int = Int.MAX_VALUE
    private var weekCnt = weekEnd - weekStart + 1
    private val textPaint = Paint()
    private val backPaint = Paint()
    private val tabPaint = Paint()
    private lateinit var backBmp: Bitmap
    private lateinit var textBmp: Bitmap
    private lateinit var brightTextBmp: Bitmap
    private var isBmpAvailable = false
    private val rect = Rect()
    private val rect2 = Rect()
    private val rectF = RectF()
    private val textColor = arrayOf(Color.GRAY, Color.WHITE)
    private var tabTop = 0f
    private var tabBottom = 1f
    private var tabSize = 1
    private var objectAnimator: ObjectAnimator
    private var animTabTop = arrayOf(0f, 0f)
    private var animTabBottom = arrayOf(0f, 0f)
    private var animToTop = true
    private val interpolator = AccelerateDecelerateInterpolator()
    var listener: WeekTabListener? = null
    private var scrolling = false
    var enableListener = true
    private var keepVisible = false
    private val animHide = AnimationUtils.loadAnimation(context, R.anim.week_tab_hide)
    private val animShow = AnimationUtils.loadAnimation(context, R.anim.week_tab_show)
    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            if (e == null) return false
            val y = e.y - paddingTop
            if (y < 0 || e.y > measuredHeight - paddingBottom)
                return false
            val i = floor(y / tabSize)
            moveTo(i, true)
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (e2 == null) return false
            val y = e2.y - paddingTop
            if (y < 0 || e2.y > measuredHeight - paddingBottom) return false
            val i = floor(y / tabSize)
            if (i == tabTop && tabBottom == i + 1) return false
            if (i >= weekCnt) return false
            tabTop = i
            tabBottom = tabTop + 1
            onTabScrolled()
            invalidate()
            if (!scrolling) scrolling = true
            return true
        }
    }
    private val gestureDetector = GestureDetector(context, gestureListener)

    private val animListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            onTabSelected()
        }

        override fun onAnimationCancel(animation: Animator?) {}

        override fun onAnimationStart(animation: Animator?) {}

    }

    private val hideAnimListener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {
            visibility = GONE
        }

        override fun onAnimationStart(animation: Animation?) {}

    }

    fun getCurrentTab(): Int {
        return floor(tabTop).toInt()
    }

    init {
        backPaint.color = Color.WHITE
        backPaint.style = Paint.Style.FILL
        backPaint.setShadowLayer(
            UIUtils.dp2px(context, 4f).toFloat(), UIUtils.dp2px(context, 1f).toFloat(),
            UIUtils.dp2px(context, 1f).toFloat(), Color.argb(20, 0, 0, 0)
        )
        backPaint.isAntiAlias = true
        textPaint.isAntiAlias = true
        tabPaint.isAntiAlias = true
        tabPaint.color = Color.parseColor("#4fc3f7")
        objectAnimator = ObjectAnimator.ofFloat(this, "animValue", 0f, 1f)
        objectAnimator.duration = 600
        objectAnimator.addListener(animListener)
        animHide.setAnimationListener(hideAnimListener)
    }

    @Keep
    private fun setAnimValue(value: Float) {
        val delay = .25f
        var t1 = min(value, 1 - delay) / (1 - delay)
        var t2 = (max(value, delay) - delay) / (1 - delay)
        if (!animToTop) {
            val t = t1
            t1 = t2
            t2 = t
        }
        tabTop = animTabTop[0] + (animTabTop[1] - animTabTop[0]) * interpolator.getInterpolation(t1)
        tabBottom =
            animTabBottom[0] + (animTabBottom[1] - animTabBottom[0]) * interpolator.getInterpolation(
                t2
            )
        keepVisible = true
        invalidate()
    }

    private fun prepareAnim(target: Float) {
        animToTop = target < (tabTop + tabBottom) / 2
        animTabTop[0] = tabTop
        animTabBottom[0] = tabBottom
        animTabTop[1] = target
        animTabBottom[1] = target + 1
        val d = 800 * (animTabTop[1] - animTabTop[0]).absoluteValue / 20
        objectAnimator.duration = max(300, d.toLong())
        if (objectAnimator.isRunning)
            objectAnimator.cancel()
    }

    private fun onTabScrolled() {
        keepVisible = true
        if (enableListener)
            listener?.onTabScrolled(tabTop)
    }

    private fun onTabSelected() {
        keepVisible = false
        if (!enableListener) return
        val v = getCurrentTab()
        listener?.onTabSelected(v)
    }

    fun hide() {
        if (keepVisible) return
        if (visibility == GONE) return
        if (animHide.hasStarted() && !animHide.hasEnded()) return
        animShow.cancel()
        animHide.reset()
        startAnimation(animHide)
    }

    fun show() {
        if (visibility == VISIBLE) return
        if (animShow.hasStarted() && !animShow.hasEnded()) return
        animHide.cancel()
        animShow.reset()
        visibility = VISIBLE
        startAnimation(animShow)
    }

    fun toggle() {
        if (visibility == VISIBLE)
            hide()
        else show()
    }

    fun moveTo(target: Float, anim: Boolean = false) {
        if (visibility != VISIBLE) {
            tabTop = target
            tabBottom = tabTop + 1
            return
        }
        if (!anim) {
            tabTop = target
            tabBottom = tabTop + 1
            invalidate()
            onTabSelected()
            return
        }
        if (objectAnimator.isRunning) {
            if (target == animTabTop[1])
                return
        } else if (target == tabTop) return
        objectAnimator.cancel()
        prepareAnim(target)
        objectAnimator.start()
    }

    private fun buildBmp() {
        isBmpAvailable = true
        val w = measuredWidth
        val h = measuredHeight
        val w1 = w - paddingLeft - paddingRight
        val h1 = h - paddingTop - paddingBottom
        backBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        textBmp = Bitmap.createBitmap(w1, h1, Bitmap.Config.ARGB_8888)
        brightTextBmp = Bitmap.createBitmap(w1, h1, Bitmap.Config.ARGB_8888)
        val backCanvas = Canvas(backBmp)
        val r = w / 2f
        rect.set(paddingLeft, paddingTop, w - paddingRight, h - paddingBottom)
        rectF.set(rect)
        backCanvas.drawRoundRect(rectF, r, r, backPaint)
        textPaint.textSize = w1 * 0.4f
        textPaint.color = textColor[0]
        drawText(textBmp)
        textPaint.color = textColor[1]
        drawText(brightTextBmp)
    }

    private fun drawText(bmp: Bitmap) {
        val textCanvas = Canvas(bmp)
        var s = ""
        val w = bmp.width
        val h = bmp.height
        for ((ii, i) in (weekStart..weekEnd).withIndex()) {
            s = i.toString()
            textPaint.getTextBounds(s, 0, s.length, rect)
            textCanvas.drawText(
                s, (w - rect.width()) / 2f,
                h * ii / weekCnt + (w + rect.height()) / 2f, textPaint
            )
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (weekCnt == 0) return
        if (!isBmpAvailable) buildBmp()
        val w = measuredWidth
        val h = measuredHeight
        val w1 = w - paddingLeft - paddingRight
        val h1 = h - paddingTop - paddingBottom
        rect.set(0, 0, backBmp.width, backBmp.height)
        canvas.drawBitmap(backBmp, null, rect, null)
        rect.set(paddingLeft, paddingTop, paddingLeft + textBmp.width, paddingTop + textBmp.height)
        canvas.drawBitmap(textBmp, null, rect, null)
        val tt = tabTop * h1 / weekCnt
        val tb = tabBottom * h1 / weekCnt
        rectF.set(
            paddingLeft.toFloat(),
            paddingTop + tt,
            (paddingLeft + w1).toFloat(),
            paddingTop + tb
        )
        val r = w1 / 2f
        tabSize = textBmp.height / weekCnt
        canvas.drawRoundRect(rectF, r, r, tabPaint)
        rect.set(0, tt.toInt(), w1, tb.toInt())
        rect2.set(
            paddingLeft,
            (paddingTop + tt).toInt(),
            paddingLeft + w1,
            (paddingTop + tb).toInt()
        )
        canvas.drawBitmap(brightTextBmp, rect, rect2, null)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) recycleBmp()
    }

    private fun recycleBmp() {
        if (!isBmpAvailable) return
        backBmp.recycle()
        brightTextBmp.recycle()
        textBmp.recycle()
        isBmpAvailable = false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        objectAnimator.cancel()
        recycleBmp()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var h = measuredHeight - paddingTop - paddingBottom
        var w = h / weekCnt
        if (w > maxWidth) {
            w = maxWidth
            h = w * weekCnt
        }
        w += paddingLeft + paddingRight
        h += paddingTop + paddingBottom
        setMeasuredDimension(w, h)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (visibility == GONE) return false
        val r = gestureDetector.onTouchEvent(ev)
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (scrolling) {
                    scrolling = false
                    onTabSelected()
                }
            }
        }
        return r
    }

    interface WeekTabListener {
        fun onTabScrolled(pos: Float)
        fun onTabSelected(pos: Int)
    }

}