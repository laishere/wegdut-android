package com.wegdut.wegdut.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLayoutChangeListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Keep
import androidx.core.animation.doOnEnd
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.badge.BadgeDrawable
import com.wegdut.wegdut.R
import com.wegdut.wegdut.utils.UIUtils
import kotlin.math.*

class TabView(context: Context, attrs: AttributeSet?) : HorizontalScrollView(context, attrs) {
    private val tabWrapper: TabWrapper = TabWrapper(context, this)

    init {
        tabWrapper.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        tabWrapper.orientation = LinearLayout.HORIZONTAL
        isHorizontalScrollBarEnabled = false
        addView(tabWrapper)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TabView)
        tabWrapper.apply {
            activeColor = ta.getColor(R.styleable.TabView_activeColor, activeColor)
            color = ta.getColor(R.styleable.TabView_color, color)
            textSize = ta.getDimensionPixelSize(R.styleable.TabView_android_textSize, textSize)
            barColor = ta.getColor(R.styleable.TabView_barColor, barColor)
            barWidth = ta.getDimensionPixelSize(R.styleable.TabView_barWidth, barWidth)
            barHeight = ta.getDimensionPixelSize(R.styleable.TabView_barHeight, barHeight)
            barOffset = ta.getDimensionPixelSize(R.styleable.TabView_barOffset, barOffset)
            maxBarWidth = ta.getDimensionPixelSize(R.styleable.TabView_maxBarWidth, maxBarWidth)
            val arr = ta.getTextArray(R.styleable.TabView_titles)
            if (arr != null) titles.addAll(arr)
            fillGap = ta.getBoolean(R.styleable.TabView_fillGap, fillGap)
        }
        ta.recycle()
        tabWrapper.initView()
    }

    fun setOnTabClickListener(onTabClickListener: OnTabClickListener) {
        tabWrapper.onTabClickListener = onTabClickListener
    }

    fun setupWithViewPager(viewPager: ViewPager?) {
        tabWrapper.setupWithViewPager(viewPager)
    }


    fun setCurrentItem(pos: Int, animation: Boolean = false) {
        tabWrapper.moveTo(pos, animation)
    }

    fun notifyTitlesChanged() {
        tabWrapper.notifyTitlesChanged()
    }

    fun setTitle(index: Int, title: CharSequence) {
        val t = tabWrapper.titles
        if (index >= t.size) t.add(title)
        else t[index] = title
        tabWrapper.onTitleChanged()
    }

    fun getOrCreateBadge(index: Int): BadgeDrawable? {
        return tabWrapper.getOrCreateBadge(index)
    }

    fun setBadgeBackgroundColor(color: Int?) {
        tabWrapper.setBadgeBackgroundColor(color)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val h = View.getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        setMeasuredDimension(w, h)
        tabWrapper.minimumWidth = w
        measureChild(tabWrapper, widthMeasureSpec, heightMeasureSpec)
    }

    interface OnTabClickListener {
        fun onClick(v: View, position: Int)
    }

    private class TabWrapper(context: Context, private val parentView: HorizontalScrollView) :
        LinearLayout(context) {
        private var tabPosition = 0f
        var selectedTab = 0
            set(value) {
                if (field in 0 until childCount)
                    applyViewState(getChildAt(field), false)
                field = value
                if (field in 0 until childCount)
                    applyViewState(getChildAt(field), true)
                onTabSelected()
            }
        var onTabClickListener: OnTabClickListener? = null
        var barColor: Int = Color.BLACK
        var activeColor: Int = Color.BLACK
        var color: Int = Color.BLACK
        var barOffset = UIUtils.dp2px(context, 5f) // 文字底部偏离量
        var barHeight = UIUtils.dp2px(context, 3f)
        var maxBarWidth = UIUtils.dp2px(context, 80f)
        var barWidth = UIUtils.dp2px(context, 16f)
        private val normalDuration = 300
        private val barBound = RectF()
        private val boundDelta = UIUtils.dp2px(context, 2f)
        private val paint = Paint()
        private val objectAnimator =
            ObjectAnimator.ofFloat(this, "animationValue", 0f)
        var textSize = UIUtils.dp2px(context, 16f)
        private var viewPager: ViewPager? = null
        var titles = mutableListOf<CharSequence>()
        var fillGap = true
        private var badgeBackgroundColor: Int? = null

        init {
            paint.isAntiAlias = true
            objectAnimator.interpolator = AccelerateDecelerateInterpolator()
            objectAnimator.duration = normalDuration.toLong()
            objectAnimator.doOnEnd {
                selectedTab = round(tabPosition).toInt()
            }
            clipChildren = false
            clipToPadding = false
        }

        fun initView() {
            populate()
        }

        fun onTitleChanged() {
            populate()
        }

        fun setBadgeBackgroundColor(color: Int?) {
            badgeBackgroundColor = color
        }

        fun getOrCreateBadge(index: Int): BadgeDrawable? {
            val item = getChildAt(index) as TabItem
            if (item.getBadge() == null) {
                val badge = BadgeDrawable.create(item.context)
                if (badgeBackgroundColor != null)
                    badge.backgroundColor = badgeBackgroundColor!!
                item.setBadge(badge)
            }
            return item.getBadge()
        }

        private fun populate() {
            val delta = titles.size - childCount
            if (delta > 0) for (i in 1..delta) addView(getTab())
            else if (delta < 0) removeViews(titles.size, -delta)
            for (i in titles.indices)
                update(getChildAt(i), titles[i])
        }

        private fun getTab(): View {
            val item = TabItem(context)
            val padding = UIUtils.dp2px(context, 16f)
            item.setPadding(padding, 0, padding, 0)
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            lp.weight = if (fillGap) 1f else 0f
            item.layoutParams = lp
            return item
        }

        private fun update(view: View, title: CharSequence) {
            val item = view as TabItem
            item.setText(title)
        }

        fun setupWithViewPager(viewPager: ViewPager?) {
            this.viewPager = viewPager
            viewPager?.let {
                it.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {
                        if (state == ViewPager.SCROLL_STATE_IDLE) {
                            moveTo(viewPager.currentItem, false)
                        }
                    }

                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                        val p = position + positionOffset
                        setAnimationValue(p)
                    }

                    override fun onPageSelected(position: Int) {
                        selectedTab = position
                    }
                })
                it.addOnAdapterChangeListener { _, _, newAdapter ->
                    onViewPagerAdapterChange(newAdapter)
                }
            }
            onViewPagerAdapterChange(viewPager?.adapter)
        }

        fun notifyTitlesChanged() {
            onViewPagerAdapterChange(viewPager?.adapter)
        }

        private fun onViewPagerAdapterChange(newAdapter: PagerAdapter?) {
            titles.clear()
            if (newAdapter != null) {
                for (i in 0 until newAdapter.count)
                    titles.add(newAdapter.getPageTitle(i)!!)
            }
            populate()
        }

        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            super.onLayout(changed, l, t, r, b)
            calculateBarBound()
        }

        override fun dispatchDraw(canvas: Canvas) {
            super.dispatchDraw(canvas)
            drawBar(canvas)
        }

        private fun drawBar(canvas: Canvas) {
            if (childCount == 0) return
            val r = barHeight / 2f
            paint.color = barColor
            canvas.drawRoundRect(barBound, r, r, paint)
        }

        @Keep
        fun setAnimationValue(t: Float) {
            tabPosition = t
            invalidateBar()
        }

        private fun calculateBarBound() {
            if (childCount == 0) return
            val t = (height + textSize) / 2f + barOffset
            val b = t + barHeight
            val i = floor(tabPosition).toInt()
            if (i !in 0 until childCount) return
            val ch = getChildAt(i)
            var l = ch.x + ch.width / 2f - barWidth / 2f
            var r = l + barWidth
            if (tabPosition + 1 < childCount) {
                val t2 = tabPosition - i
                val ch2 = getChildAt(i + 1)
                val l2 = ch2.x + ch2.width / 2f - barWidth / 2f
                val r2 = l2 + barWidth
                r += (r2 - r) * min(1f, 2 * t2)
                l += (l2 - l) * max(0f, 2 * t2 - 1f)
                l = max(l, r - maxBarWidth)
            }
            barBound.set(l, t, r, b)
        }

        private fun invalidateBar() {
            calculateBarBound()
            val l2 = (barBound.left - boundDelta).toInt()
            val t2 = (barBound.top - boundDelta).toInt()
            val r2 = (barBound.right + boundDelta).toInt()
            val b2 = (barBound.bottom + boundDelta).toInt()
            postInvalidateOnAnimation(l2, t2, r2, b2)
        }

        fun moveTo(index: Int, smooth: Boolean = true) {
            if (smooth) {
                objectAnimator.cancel()
                if (abs(tabPosition - index) > 1f) {
                    val d = 0.3f
                    tabPosition = index + if (index > tabPosition) -d
                    else d
                }
                objectAnimator.setFloatValues(tabPosition, index.toFloat())
                objectAnimator.start()
            } else {
                setAnimationValue(index.toFloat())
                selectedTab = index
            }
        }

        private fun onTabSelected() {
            if (selectedTab !in 0 until childCount) return
            val selectedChild = getChildAt(selectedTab)
            val cx = selectedChild.x + selectedChild.width / 2f
            val visibleWidth = parentView.width - parentView.paddingLeft - parentView.paddingRight
            val toCx = visibleWidth / 2f
            var sx = cx - toCx
            val scrollWidth = width
            sx = max(0f, sx)
            sx = min((scrollWidth - visibleWidth).toFloat(), sx)
            parentView.smoothScrollTo(sx.toInt(), parentView.scrollY)
        }

        override fun addView(child: View) {
            super.addView(child)
            val i = childCount - 1
            applyViewState(child, i == selectedTab)
            child.setOnClickListener(clickListener)
        }

        private val clickListener = OnClickListener {
            val i = indexOfChild(it)
            if (i >= 0) {
                if (viewPager == null) moveTo(i)
                else viewPager!!.currentItem = i
                onTabClickListener?.onClick(it, i)
            }
        }

        private fun applyViewState(child: View, active: Boolean) {
            val item = child as TabItem
            val tv = item.textView
            if (active) tv.setTextColor(activeColor)
            else tv.setTextColor(color)
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }
    }

    private class TabItem(context: Context) : FrameLayout(context) {
        val textView = TextView(context)
        private var badge: BadgeDrawable? = null
        private var badgeParent: FrameLayout? = null
        private val onLayoutChangedListener =
            OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateBadge()
            }

        init {
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            lp.gravity = Gravity.CENTER
            textView.layoutParams = lp
            addView(textView)
            textView.addOnLayoutChangeListener(onLayoutChangedListener)
            clipChildren = false
            clipToPadding = false
        }

        fun setText(text: CharSequence) {
            textView.text = text
        }

        fun setBadge(badge: BadgeDrawable?) {
            if (this.badge == badge) return
            this.badge = badge
            if (badge != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    textView.overlay.clear()
                    textView.overlay.add(badge)
                    badgeParent = null
                } else {
                    badgeParent = this
                    foreground = badge
                }
                updateBadge()
            }
        }

        private fun updateBadge() {
            val badge = this.badge ?: return
            val rect = Rect()
            textView.getDrawingRect(rect)
            badge.bounds = rect
            badge.updateBadgeCoordinates(textView, badgeParent)
        }

        fun getBadge() = badge
    }
}