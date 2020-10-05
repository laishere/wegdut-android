package com.wegdut.wegdut.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.Keep
import androidx.core.animation.doOnStart
import androidx.core.graphics.ColorUtils
import com.wegdut.wegdut.R
import com.wegdut.wegdut.utils.UIUtils
import kotlin.math.*
import kotlin.random.Random

class ShineButton : View {
    constructor(context: Context?, drawable: Drawable) : super(context) {
        setAttrs(null)
        this.drawable = drawable
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setAttrs(attrs)
    }

    // const
    private val drawableHeightFactor = 1.25f
    private val colorGreyFactor = 0.85f
    private var pointEffectGapAngleRange = arrayOf(0.2f, 0.4f)
    private var pointEffectStartAngleRange = arrayOf(0f, 1f)
    private var pointEffectTurningAngleRange = arrayOf(0.2f, 0.4f)
    private var pointEffectSmallPointRadiusRange = arrayOf(0.75f, 0.96f)

    // size
    private var minWidth = 0
    private var minHeight = 0
    private var drawableWidth = 0
    private var drawableHeight = 0
    private val drawableBounds = Rect()
    private val textBounds = Rect()
    private lateinit var colorFilter: ColorFilter
    var textSize: Float = 0f
        set(value) {
            field = value
            paint.textSize = value
            calculateSize()
            requestLayout()
        }
    private val gap get() = (textSize * 0.4f).toInt()
    private val effectDistance get() = textSize

    // draw
    var text: String = ""
        set(value) {
            field = value
            calculateSize()
            requestLayout()
        }
    private lateinit var scaleDrawable: ScaleDrawable
    var drawable: Drawable = ColorDrawable(Color.WHITE) // 只作占位用途
        set(value) {
            field = value
            scaleDrawable = ScaleDrawable(drawable, Gravity.CENTER, 1f, 1f)
            calculateSize()
            requestLayout()
        }
    var color: Int = Color.BLACK
    var activeColor: Int = Color.BLACK
        set(value) {
            field = value
            greyActiveColor = getGreyColor(activeColor)
        }
    var greyActiveColor = getGreyColor(activeColor)
    private val paint = Paint()
    private var activeAnim: Boolean = true

    // status
    var isActive: Boolean = false
        set(value) {
            field = value
            val c = if (isActive) activeColor else color
            colorFilter = PorterDuffColorFilter(c, PorterDuff.Mode.SRC_IN)
            if (activeAnim) {
                if (!isActive) objectAnimator.cancel()
                else if (!objectAnimator.isRunning)
                    objectAnimator.start()
            } else objectAnimator.end()
            invalidate()
        }

    fun setActive(active: Boolean, anim: Boolean) {
        activeAnim = anim
        isActive = active
    }

    fun setState(state: ShineButtonState) {
        text = state.text
        setActive(state.active, state.hasAnimation)
    }

    // animation
    private val objectAnimator = ObjectAnimator()
    private var animatorValue: Float = 0f
    private var drawableScaleAnimatorValue = 0f
    private var circleSizeAnimatorValue = 0f
    private var effectAnimatorValue = 0f
    private val drawableScaleAnimation =
        arrayOf(
            0.3f, 0.5f, //0.3之前为1f
            0.53f, 1f,
            0.76f, 0.85f,
            1f, 1f
        )
    private val circleSizeAnimation =
        arrayOf(
            0f, 0f,
            0.3f, 1f
        )
    private val effectAnimation =
        arrayOf(
            0.3f, 0f, // 直到0.5才绘制
            1f, 1f
        )

    // point
    private var pointEffectGapAngle: Float = 0f
    private var pointEffectStartAngle: Float = 0f
    private var pointEffectTurningAngle: Float = 0f
    private val pointEffectPointCount = 8
    private val bigPointSize get() = textSize * 0.2f
    private val smallPointSize get() = bigPointSize * 0.5f
    private var pointEffectSmallPointRadiusFactor = 0f

    // line
    private val lineEffectMaxLength get() = effectDistance * 0.5f
    private val lineEffectWidth = UIUtils.dp2px(context, 1f).toFloat()
    private val lineEffectLineCount = 10

    var effectType: EffectType = EffectType.LINE
    var duration: Long = 1000
    var interpolator: Interpolator = DecelerateInterpolator()
        set(value) {
            field = value
            objectAnimator.interpolator = interpolator
        }

    private fun setAttrs(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ShineButton)
        text = ta.getString(R.styleable.ShineButton_text) ?: ""
        textSize = ta.getDimension(
            R.styleable.ShineButton_textSize,
            UIUtils.dp2px(context, 16f).toFloat()
        )
        drawable = ta.getDrawable(R.styleable.ShineButton_icon) ?: drawable
        color = ta.getColor(R.styleable.ShineButton_color, color)
        activeColor = ta.getColor(R.styleable.ShineButton_activeColor, activeColor)
        effectType = EffectType.values()[ta.getInt(R.styleable.ShineButton_effectType, 0)]
        ta.recycle()
        paint.textSize = textSize
        paint.isAntiAlias = true
        calculateSize()
        objectAnimator.setFloatValues(0f, 1f)
        objectAnimator.target = this
        objectAnimator.setPropertyName("animatorValue")
        objectAnimator.doOnStart { prepareEffect() }
        objectAnimator.duration = duration
        scaleDrawable = ScaleDrawable(drawable, Gravity.CENTER, 1f, 1f)
        isActive = false
    }

    private fun calculateSize() {
        paint.getTextBounds(text, 0, text.length, textBounds)
        val dw = drawable.intrinsicWidth
        val dh = drawable.intrinsicHeight
        drawableHeight = (textSize * drawableHeightFactor).toInt()
        drawableWidth = dw * drawableHeight / dh
        val fixedGap = if (text.isBlank()) 0 else gap
        minWidth =
            paddingLeft + paddingRight + drawableWidth + fixedGap + textBounds.right + textBounds.left
        minHeight = paddingTop + paddingBottom + max(drawableHeight, textBounds.height())
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        drawEffect(canvas)
        drawDrawable(canvas)
        drawCircle(canvas)
        val l = paddingLeft + drawableWidth + gap
        paint.color = if (isActive) activeColor else color
        canvas.drawText(
            text,
            l.toFloat(),
            (height + textBounds.height()) / 2f - textBounds.bottom,
            paint
        )
    }

    private fun drawCircle(canvas: Canvas) {
        if (!isActive || circleSizeAnimatorValue < 0) return
        val cx = paddingLeft + drawableWidth / 2f
        val cy = height / 2f
        val a = drawableWidth.toFloat()
        val b = drawableHeight.toFloat()
        val r = sqrt(a * a + b * b) / 2 * circleSizeAnimatorValue
        paint.color = Color.WHITE
        canvas.drawCircle(cx, cy, r, paint)
        paint.strokeWidth = smallPointSize
        paint.color = activeColor
        paint.style = Paint.Style.STROKE
        canvas.drawCircle(cx, cy, r, paint)
        paint.style = Paint.Style.FILL
    }

    private fun drawDrawable(canvas: Canvas) {
        val l = paddingLeft
        val scale = when {
            drawableScaleAnimatorValue < 0f || !isActive -> 1f
            else -> drawableScaleAnimatorValue
        }
        drawableBounds.left = l
        drawableBounds.top = (height - drawableHeight) / 2
        drawableBounds.right = drawableBounds.left + drawableWidth
        drawableBounds.bottom = drawableBounds.top + drawableHeight
        drawable.mutate().colorFilter = colorFilter
        scaleDrawable.bounds = drawableBounds
        scaleDrawable.level = (10000 * scale).toInt()
        scaleDrawable.draw(canvas)
    }

    private fun drawEffect(canvas: Canvas) {
        if (!isActive || effectAnimatorValue == 1f || effectAnimatorValue < 0) return
        when (effectType) {
            EffectType.POINT -> drawPointsEffect(canvas)
            EffectType.LINE -> drawLinesEffect(canvas)
        }
    }

    private fun randomFloat(arr: Array<Float>): Float {
        val from = arr[0]
        val to = arr[1]
        return from + (to - from) * Random.nextFloat()
    }

    private fun prepareEffect() {
        when (effectType) {
            EffectType.POINT -> {
                pointEffectGapAngle = randomFloat(pointEffectGapAngleRange)
                pointEffectStartAngle = randomFloat(pointEffectStartAngleRange)
                pointEffectTurningAngle = randomFloat(pointEffectTurningAngleRange)
                pointEffectSmallPointRadiusFactor = randomFloat(pointEffectSmallPointRadiusRange)
            }
            EffectType.LINE -> {

            }
        }
    }

    private fun drawPointsEffect(canvas: Canvas) {
        val a = drawableWidth.toFloat()
        val b = drawableHeight.toFloat()
        val r = sqrt(a * a + b * b) / 2
        val cx = paddingLeft + a / 2
        val cy = height / 2f
        val t = effectAnimatorValue
        val p = r + effectDistance * t
        val sp = p * pointEffectSmallPointRadiusFactor
        val bigSize = bigPointSize * (1f - t)
        val smallSize = smallPointSize * (1f - t)
        for (i in 0 until pointEffectPointCount) {
            var ang: Float =
                (i * Math.PI * 2 / pointEffectPointCount + pointEffectStartAngle + pointEffectTurningAngle * t).toFloat()
            var x = sp * cos(ang) + cx
            var y = sp * sin(ang) + cy
            paint.color = greyActiveColor
            canvas.drawCircle(x, y, smallSize, paint)
            ang += pointEffectGapAngle
            x = p * cos(ang) + cx
            y = p * sin(ang) + cy
            paint.color = activeColor
            canvas.drawCircle(x, y, bigSize, paint)
        }
    }

    private fun drawLinesEffect(canvas: Canvas) {
        val a = drawableWidth.toFloat()
        val b = drawableHeight.toFloat()
        val r = sqrt(a * a + b * b) / 2
        val cx = paddingLeft + a / 2
        val cy = height / 2f
        val t = effectAnimatorValue
        val l = lineEffectMaxLength * (1f - effectAnimatorValue)
        val p = r + effectDistance * t
        val p0 = p - l
        paint.color = activeColor
        paint.strokeWidth = lineEffectWidth
        paint.style = Paint.Style.STROKE
        for (i in 0 until lineEffectLineCount) {
            val ang = (i * Math.PI * 2 / lineEffectLineCount).toFloat()
            val x1 = p0 * cos(ang) + cx
            val y1 = p0 * sin(ang) + cy
            val x2 = p * cos(ang) + cx
            val y2 = p * sin(ang) + cy
            canvas.drawLine(x1, y1, x2, y2, paint)
        }
        paint.style = Paint.Style.FILL
    }

    @Keep
    private fun setAnimatorValue(value: Float) {
        animatorValue = value
        drawableScaleAnimatorValue = calculateAnimatorValue(drawableScaleAnimation)
        circleSizeAnimatorValue = calculateAnimatorValue(circleSizeAnimation)
        effectAnimatorValue = calculateAnimatorValue(effectAnimation)
        invalidate()
    }

    private fun calculateAnimatorValue(arr: Array<Float>): Float {
        var ii = -1
        val t = animatorValue
        for (i in arr.indices step 2) {
            val r = arr[i]
            ii = i
            if (r < 0f && -r > t) {  // 当前，负数用来分段
                ii = -1
                break
            }
            if (r > t) break
        }
        return when {
            ii < 2 || arr[ii] < t -> -1f
            else -> {
                val l = arr[ii - 2]
                val lv = arr[ii - 1]
                val r = arr[ii]
                val rv = arr[ii + 1]
                lv + (t - l) / (r - l) * (rv - lv)
            }
        }
    }

    private fun getGreyColor(color: Int): Int {
        return ColorUtils.blendARGB(Color.BLACK, color, colorGreyFactor)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getSize(suggestedMinimumWidth, widthMeasureSpec),
            getSize(suggestedMinimumHeight, heightMeasureSpec)
        )
    }

    private fun getSize(size: Int, measureSpec: Int): Int {
        var result = size
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.UNSPECIFIED -> result = size
            MeasureSpec.AT_MOST -> min(specSize, size)
            MeasureSpec.EXACTLY -> result = specSize
        }
        return result
    }

    override fun getSuggestedMinimumWidth(): Int {
        return minWidth
    }

    override fun getSuggestedMinimumHeight(): Int {
        return minHeight
    }

    enum class EffectType {
        LINE, POINT
    }

    data class ShineButtonState(
        val active: Boolean,
        val text: String,
        val hasAnimation: Boolean
    )
}