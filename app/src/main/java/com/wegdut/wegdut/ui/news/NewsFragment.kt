package com.wegdut.wegdut.ui.news

import android.os.Bundle
import android.view.View
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.BaseDaggerFragment
import com.wegdut.wegdut.ui.Reselectable
import javax.inject.Inject

open class NewsFragment : BaseDaggerFragment(R.layout.fragment_school_news), Reselectable {

    @Inject
    lateinit var newsView: NewsView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsView.bind(view, activity!!)
    }

    override fun onDestroyView() {
        newsView.unbind()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsView.start()
    }

    override fun onDestroy() {
        newsView.stop()
        super.onDestroy()
    }

    override fun reselect() {
        newsView.reselect()
    }
}