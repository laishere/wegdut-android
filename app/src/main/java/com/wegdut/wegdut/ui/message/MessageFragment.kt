package com.wegdut.wegdut.ui.message


import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.wegdut.wegdut.MainActivity
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.message.MessageUnread
import com.wegdut.wegdut.ui.BaseDaggerFragment
import com.wegdut.wegdut.ui.Reselectable
import com.wegdut.wegdut.ui.SimpleFragmentWithTitleAdapter
import com.wegdut.wegdut.ui.message.like.LikeFragment
import com.wegdut.wegdut.ui.message.reply.ReplyFragment
import com.wegdut.wegdut.ui.message.system.SystemFragment
import com.wegdut.wegdut.utils.UIUtils
import com.wegdut.wegdut.view.TabView
import javax.inject.Inject

class MessageFragment : BaseDaggerFragment(R.layout.fragment_message), Reselectable,
    MessageContract.View {
    private lateinit var tabView: TabView
    private lateinit var fragments: Array<Fragment>
    private lateinit var viewPager: ViewPager

    @Inject
    lateinit var presenter: MessageContract.Presenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UIUtils.addStatusBarPadding(view)
        tabView = view.findViewById(R.id.tab_view)
        viewPager = view.findViewById(R.id.view_pager)
        fragments = arrayOf(
            ReplyFragment(),
            LikeFragment(),
            SystemFragment()
        )
        val titles = arrayOf(
            R.string.notification_title_reply,
            R.string.notification_title_like,
            R.string.notification_title_system
        ).map { getString(it) }
        val pagerAdapter =
            SimpleFragmentWithTitleAdapter(childFragmentManager, fragments, titles.toTypedArray())
        viewPager.adapter = pagerAdapter
        tabView.setupWithViewPager(viewPager)
        val badgeColor = ResourcesCompat.getColor(resources, R.color.badge_color, null)
        tabView.setBadgeBackgroundColor(badgeColor)
        presenter.subscribe(this)
    }

    override fun onStart() {
        super.onStart()
        presenter.updateUnread()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribe()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.start()
    }

    override fun onDestroy() {
        presenter.stop()
        super.onDestroy()
    }

    override fun reselect() {
        val fragment = fragments.getOrNull(viewPager.currentItem)
        if (fragment is Reselectable)
            fragment.reselect()
    }

    override fun setMessageUnread(messageUnread: MessageUnread) {
        val unreadArr = BooleanArray(3) { false }
        val unread = messageUnread.run {
            unreadArr[0] = reply > 0
            unreadArr[1] = like > 0
            unreadArr[2] = system > 0
            like > 0 || reply > 0 || system > 0
        }
        for (i in unreadArr.withIndex())
            tabView.getOrCreateBadge(i.index)?.isVisible = i.value
        activity?.let {
            val act = it as MainActivity
            act.setMessageUnread(unread)
        }
    }
}