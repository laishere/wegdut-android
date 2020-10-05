package com.wegdut.wegdut.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.wegdut.wegdut.R

class HeightRatioFrameLayout(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private val ratio: Float

    init {
        val ta: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.HeightRatioFrameLayout)
        ratio = ta.getFloat(R.styleable.HeightRatioFrameLayout_ratio, 1f)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val h = (w * ratio).toInt() + paddingTop + paddingBottom
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY))
    }
}