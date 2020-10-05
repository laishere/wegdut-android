package com.wegdut.wegdut.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import kotlin.math.max

class FitMatchParentLinearLayout(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = MeasureSpec.getSize(heightMeasureSpec) // 必须传递父高度
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED)
        )
        var h = 0
        for (ch in children) {
            if (ch.layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                ch.measure(
                    MeasureSpec.makeMeasureSpec(ch.measuredWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                )
            }
            h += ch.measuredHeight
        }
        setMeasuredDimension(measuredWidth, max(measuredHeight, h))
    }
}