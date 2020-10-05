package com.wegdut.wegdut.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.NestedScrollingChild3
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.ViewCompat

class MyCoordinatorLayout(context: Context, attrs: AttributeSet?) :
    CoordinatorLayout(context, attrs), NestedScrollingChild3, NestedScrollingParent3 {

    private val mChildHelper = NestedScrollingChildHelper(this)


    init {
        isNestedScrollingEnabled = true
    }

    // NestedScrollingChild3
    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int, consumed: IntArray
    ) {
        mChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow, type, consumed
        )
    }

    // NestedScrollingChild2
    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return mChildHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        mChildHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return mChildHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow, type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    // NestedScrollingChild
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return startNestedScroll(axes, ViewCompat.TYPE_TOUCH)
    }

    override fun stopNestedScroll() {
        stopNestedScroll(ViewCompat.TYPE_TOUCH)
    }

    override fun hasNestedScrollingParent(): Boolean {
        return hasNestedScrollingParent(ViewCompat.TYPE_TOUCH)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
        dyUnconsumed: Int, offsetInWindow: IntArray?
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
            dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
            offsetInWindow
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, ViewCompat.TYPE_TOUCH)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedScrollAccepted(
        child: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ) {
        super.onNestedScrollAccepted(child, target, nestedScrollAxes, type)
        startNestedScroll(nestedScrollAxes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        super.onStopNestedScroll(target, type)
        stopNestedScroll(type)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val consumed2 = IntArray(2)
        if (dy > 0) {
            dispatchNestedPreScroll(dx, dy, consumed, null, type)
            super.onNestedPreScroll(target, dx - consumed[0], dy - consumed[1], consumed2, type)
        } else {
            super.onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
            dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], consumed2, null, type)
        }
        consumed[0] += consumed2[0]
        consumed[1] += consumed2[1]
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            null,
            type,
            consumed
        )
        super.onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed - consumed[0],
            dyUnconsumed - consumed[1],
            type,
            consumed
        )
    }
}