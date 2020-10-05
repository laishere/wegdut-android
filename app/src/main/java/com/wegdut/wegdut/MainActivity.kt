package com.wegdut.wegdut

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wegdut.wegdut.ui.Reselectable
import com.wegdut.wegdut.ui.SimpleFragmentAdapter
import com.wegdut.wegdut.ui.home.HomeFragment
import com.wegdut.wegdut.ui.message.MessageFragment
import com.wegdut.wegdut.ui.post.PostFragment
import com.wegdut.wegdut.ui.update.UpdateView
import com.wegdut.wegdut.ui.user.UserFragment
import com.wegdut.wegdut.utils.UIUtils

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var navView: BottomNavigationView
    private var badgeColor: Int = 0
    private val updateView = UpdateView(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragments: Array<Fragment> = arrayOf(
            HomeFragment(),
            PostFragment(),
            MessageFragment(),
            UserFragment()
        )
        val adapter = SimpleFragmentAdapter(
            supportFragmentManager,
            fragments
        )
        viewPager = findViewById(R.id.view_pager)
        viewPager.offscreenPageLimit = 4
        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemReselectedListener {
            val fragment = fragments.getOrNull(viewPager.currentItem)
            if (fragment is Reselectable)
                fragment.reselect()
        }
        viewPager.adapter = adapter
        UIUtils.bindNavAndViewPager(
            navView, viewPager,
            arrayOf(
                R.id.navigation_home, R.id.navigation_explore,
                R.id.navigation_notifications, R.id.navigation_account
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val view: View = findViewById(android.R.id.content)
            view.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        badgeColor = ResourcesCompat.getColor(resources, R.color.badge_color, null)
        updateView.checkUpdate(quiet = true)
    }

    override fun onDestroy() {
        updateView.stop()
        super.onDestroy()
    }

    fun setMessageUnread(unread: Boolean) {
        navView.getOrCreateBadge(R.id.navigation_notifications).apply {
            isVisible = unread
            backgroundColor = badgeColor
        }
    }
}
