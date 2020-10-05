package com.wegdut.wegdut.ui.photo_view

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.core.graphics.ColorUtils
import androidx.core.util.set
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.rd.PageIndicatorView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.utils.UIUtils
import kotlin.math.absoluteValue

class PhotoViewActivity : AppCompatActivity() {

    companion object {
        const val IMAGES = "IMAGES"
        const val SELECTED_IMAGE = "SELECTED_IMAGE"
        const val NO_EXIT_TRANSITION = "NO_EXIT_TRANSITION"
        fun getTransitionName(position: Int): String {
            return "TRANSITION_$position"
        }

        fun getIndexByTransitionName(name: String): Int {
            return name.split("_").last().toInt()
        }
    }

    private lateinit var bgColors: MutableList<Int>
    private lateinit var viewPager: ViewPager2
    private var isTransitionStarted = false
    private val imageViews = SparseArray<View>()
    private lateinit var root: View
    private var isExiting = false
    private var noExitTransition = false
    private val onPreDrawListener = ViewTreeObserver.OnPreDrawListener {
        if (isTransitionStarted) return@OnPreDrawListener true
        supportStartPostponedEnterTransition()
        isTransitionStarted = true
        true
    }

    override fun supportFinishAfterTransition() {
        isExiting = true
        super.supportFinishAfterTransition()
    }

    private val sharedElementCallback = object : SharedElementCallback() {
        override fun onMapSharedElements(
            names: MutableList<String>?,
            sharedElements: MutableMap<String, View>?
        ) {
            if (names != null && sharedElements != null) {
                names.clear()
                sharedElements.clear()
                if (isExiting && noExitTransition) return
                val view = imageViews[viewPager.currentItem]
                val name = getTransitionName(viewPager.currentItem)
                names.add(name)
                sharedElements[name] = view!!
                ViewCompat.setTransitionName(view, name)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportPostponeEnterTransition()
        setEnterSharedElementCallback(sharedElementCallback)
        setTheme(R.style.AppTheme_PhotoViewActivity)
        setContentView(R.layout.activity_photo_view)
        viewPager = findViewById(R.id.view_pager)
        root = window.decorView.findViewById(android.R.id.content)
        viewPager.offscreenPageLimit = 2
        setFullScreen(true)
        val images: Array<String> = intent.getStringArrayExtra(IMAGES)!!
        bgColors = MutableList(images.size) { Color.BLACK }
        val selected: Int = intent.getIntExtra(SELECTED_IMAGE, 0)
        noExitTransition = intent.getBooleanExtra(NO_EXIT_TRANSITION, noExitTransition)
        val adapter = PhotoAdapter(selected, onPreDrawListener)
        adapter.onColorReadyListener = object : PhotoAdapter.OnColorReadyListener {
            override fun onReady(position: Int, color: Int) {
                bgColors[position] = color
                if (viewPager.currentItem == position)
                    viewPager.setBackgroundColor(color)
            }
        }
        adapter.items = images.toList()
        adapter.onBindListener = object : PhotoAdapter.OnBindListener {
            override fun onBind(view: View, position: Int) {
                imageViews[position] = view
            }
        }
        viewPager.adapter = adapter
        val pageIndicatorView: PageIndicatorView = findViewById(R.id.indicator)
        pageIndicatorView.count = images.size
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                val l = bgColors[position]
                var next = position
                if (positionOffset > 0) next++
                else if (positionOffset < 0) next--
                val r = bgColors[next]
                val color = ColorUtils.blendARGB(l, r, positionOffset.absoluteValue)
                viewPager.setBackgroundColor(color)
            }

            override fun onPageSelected(position: Int) {
                pageIndicatorView.selection = position
                viewPager.setBackgroundColor(bgColors[position])
            }
        })
        viewPager.setOnSystemUiVisibilityChangeListener { setFullScreen(isFullScreen()) }
        adapter.onClickListener = View.OnClickListener {
            supportFinishAfterTransition()
        }
        viewPager.setCurrentItem(selected, false)
    }

    private fun isFullScreen(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            viewPager.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN != 0
        else false
    }

    private fun setFullScreen(fullScreen: Boolean) {
        if (fullScreen == isFullScreen()) return
        UIUtils.imageFullScreen(root, fullScreen)
    }
}