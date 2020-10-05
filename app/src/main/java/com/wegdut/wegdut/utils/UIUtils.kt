package com.wegdut.wegdut.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.util.SparseIntArray
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wegdut.wegdut.R


object UIUtils {

    fun showKeyboard(activity: Activity, view: View) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        activity.currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun displayBackInToolbar(activity: FragmentActivity, toolbar: Toolbar) {
        val drawable = ActivityCompat.getDrawable(activity, R.drawable.abc_ic_ab_back_material)!!
        drawable.colorFilter = PorterDuffColorFilter(
            getAttrColor(activity, R.attr.colorControlNormal),
            PorterDuff.Mode.SRC_IN
        )
        toolbar.navigationIcon = drawable
        toolbar.setNavigationOnClickListener {
            activity.supportFinishAfterTransition()
        }
    }

    fun bindNavAndViewPager(
        navView: BottomNavigationView,
        viewPager: ViewPager,
        menus: Array<Int>
    ) {
        val id2position = SparseIntArray()
        for (it in menus.withIndex())
            id2position.put(it.value, it.index)
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                navView.selectedItemId = menus[position]
            }
        })

        navView.setOnNavigationItemSelectedListener {
            viewPager.setCurrentItem(id2position[it.itemId], false)
            true
        }
    }

    fun addStatusBarPadding(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            // run twice, ignore latter 0
            if (insets.systemWindowInsetTop == 0) return@setOnApplyWindowInsetsListener insets
            ViewCompat.setPaddingRelative(view, 0, insets.systemWindowInsetTop, 0, 0);
            insets.replaceSystemWindowInsets(
                insets.systemWindowInsetLeft,
                0,
                insets.systemWindowInsetRight,
                insets.systemWindowInsetBottom
            )
        }
        ViewCompat.requestApplyInsets(view)
    }

    fun dp2px(context: Context, dp: Float): Int {
        return (context.resources.displayMetrics.density * dp + 0.5f).toInt()
    }

    fun setSwipeRefreshColor(swipeRefreshLayout: SwipeRefreshLayout) {
        val res = swipeRefreshLayout.resources
        val a = res.getColor(R.color.blue_500)
        val b = res.getColor(R.color.blue_700)
        val c = res.getColor(R.color.deep_purple_400)
        swipeRefreshLayout.setColorSchemeColors(a, b, c)
    }

    fun actionBarSize(context: Context): Int {
        val tv = TypedValue()
        if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
        }
        return 0
    }

    fun applyScrollElevation(appBarLayout: View, recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                appBarLayout.isHovered = recyclerView.canScrollVertically(-1)
            }
        })
    }

    fun applyScrollElevation(
        appBarLayout: View,
        nestedScrollView: NestedScrollView
    ) {
        nestedScrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
            appBarLayout.isHovered = nestedScrollView.canScrollVertically(-1)
        }
    }

    fun layoutFullScreen(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val flags = view.systemUiVisibility
            view.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    fun imageFullScreen(view: View, fullscreen: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            var flags: Int
            if (fullscreen) {
                flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                    flags = flags or View.SYSTEM_UI_FLAG_IMMERSIVE
            } else {
                flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
            view.systemUiVisibility = flags
        }
    }

    fun getAttrColor(context: Context, attr: Int): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(attr, value, true)
        try {
            return ContextCompat.getColor(context, value.resourceId)
        } catch (_: Resources.NotFoundException) {
        }
        return 0
    }

    fun crossFade(show: View, hide: View) {
        val fadeIn = AnimationUtils.loadAnimation(show.context, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(hide.context, R.anim.fade_out)
        show.visibility = View.VISIBLE
        val listener = object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                fadeOut.setAnimationListener(null)
                hide.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {}
        }
        fadeOut.setAnimationListener(listener)
        show.startAnimation(fadeIn)
        hide.startAnimation(fadeOut)
    }

    fun fade(view: View, show: Boolean) {
        view.visibility = View.VISIBLE
        view.animation?.cancel()
        if (show) {
            val fadeIn = AnimationUtils.loadAnimation(view.context, R.anim.fade_in)
            view.startAnimation(fadeIn)
            return
        }
        val fadeOut = AnimationUtils.loadAnimation(view.context, R.anim.fade_out)
        val listener = object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                fadeOut.setAnimationListener(null)
                view.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {}
        }
        fadeOut.setAnimationListener(listener)
        view.startAnimation(fadeOut)
    }
}