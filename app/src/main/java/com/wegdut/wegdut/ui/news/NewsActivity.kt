package com.wegdut.wegdut.ui.news

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.Reselectable
import com.wegdut.wegdut.ui.SimpleFragmentWithTitleAdapter
import com.wegdut.wegdut.ui.news.annotation.AnnotationFragment
import com.wegdut.wegdut.ui.news.newsletter.NewsletterFragment
import com.wegdut.wegdut.ui.news.notification.NotificationFragment
import com.wegdut.wegdut.ui.news.tender.TenderFragment
import com.wegdut.wegdut.utils.UIUtils
import dagger.android.support.DaggerAppCompatActivity

class NewsActivity : DaggerAppCompatActivity() {
    private lateinit var fragments: Array<Fragment>
    private lateinit var viewPager: ViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_news)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        viewPager = findViewById(R.id.view_pager)
        fragments = arrayOf(
            NotificationFragment(),
            AnnotationFragment(),
            NewsletterFragment(),
            TenderFragment()
        )
        val adapter = SimpleFragmentWithTitleAdapter(
            supportFragmentManager, fragments,
            arrayOf("通知", "公告", "简讯", "招标")
        )
        viewPager.adapter = adapter
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
        val appBarLayout: AppBarLayout = findViewById(R.id.app_bar_layout)
        appBarLayout.isHovered = true
        tabLayout.addOnTabSelectedListener(onTabSelectedListener)
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
            val fragment = fragments.getOrNull(viewPager.currentItem)
            if (fragment is Reselectable)
                fragment.reselect()
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {}

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
    }
}