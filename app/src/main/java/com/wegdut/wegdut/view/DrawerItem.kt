package com.wegdut.wegdut.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.core.view.children

class DrawerItem(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val objectAnimator = ObjectAnimator.ofFloat(this, "animatorValue", 0f, 1f)
    var isExtended = false
        private set
    private var currentAnimatorValue = 0f
    var onChangeListener: OnChangeListener? = null

    init {
        objectAnimator.duration = 200L
        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                setLayerType(View.LAYER_TYPE_NONE, null)
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }
        })
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (childCount == 2)
            child.setOnClickListener { toggle() }
    }

    private fun toggle() {
        setExtended(!isExtended)
    }

    fun setExtended(isExtended: Boolean, smooth: Boolean = true) {
        if (childCount < 2) return
        if (this.isExtended == isExtended) return
        objectAnimator.cancel()
        if (!isExtended) {
            objectAnimator.setFloatValues(currentAnimatorValue, 0f)
        } else {
            val w = getChildAt(0).measuredWidth
            objectAnimator.setFloatValues(currentAnimatorValue, -w.toFloat())
        }
        this.isExtended = isExtended
        if (smooth) objectAnimator.start()
        else objectAnimator.end()
        onChangeListener?.onChange(isExtended)
    }

    @Keep
    private fun setAnimatorValue(t: Float) {
        currentAnimatorValue = t
        val c = getChildAt(1)
        c.translationX = t
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val h = measuredHeight - paddingTop - paddingBottom
        for (c in children) {
            val w = c.measuredWidth
            val wSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY)
            val hSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY)
            c.measure(wSpec, hSpec)
        }
    }

    interface OnChangeListener {
        fun onChange(isExtended: Boolean)
    }
}