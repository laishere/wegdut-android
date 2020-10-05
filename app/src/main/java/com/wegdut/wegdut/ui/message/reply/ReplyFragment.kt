package com.wegdut.wegdut.ui.message.reply

import android.os.Bundle
import android.view.View
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.BaseDaggerFragment
import com.wegdut.wegdut.ui.Reselectable
import javax.inject.Inject

class ReplyFragment : BaseDaggerFragment(R.layout.fragment_notification_reply), Reselectable {
    @Inject
    lateinit var messageView: ReplyMessageContract.View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageView.bind(view, activity!!)
    }

    override fun onDestroyView() {
        messageView.unbind()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messageView.start()
    }

    override fun onDestroy() {
        messageView.stop()
        super.onDestroy()
    }

    override fun reselect() {
        messageView.reselect()
    }
}