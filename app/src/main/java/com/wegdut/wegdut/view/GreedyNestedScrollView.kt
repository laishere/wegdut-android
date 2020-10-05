package com.wegdut.wegdut.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView

class GreedyNestedScrollView(context: Context, attrs: AttributeSet?) :
    NestedScrollView(context, attrs) {

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0 && canScrollVertically(dy)) {
            val consumed2 = IntArray(2)
            dispatchNestedPreScroll(dx, dy, consumed2, null, type)
            scrollBy(0, dy - consumed2[1])
            consumed[0] = consumed2[0]
            consumed[1] = dy
            return
        }
        super.onNestedPreScroll(target, dx, dy, consumed, type)
    }

    override fun measureChildWithMargins(
        child: View?,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ) {

        val lp = child!!.layoutParams as MarginLayoutParams

        val childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(
            parentWidthMeasureSpec,
            paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin
                    + widthUsed, lp.width
        )
        val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
            parentHeightMeasureSpec,
            paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin
                    + heightUsed, lp.height
        )

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }
}