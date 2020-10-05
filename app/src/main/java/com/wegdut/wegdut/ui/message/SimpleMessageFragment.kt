package com.wegdut.wegdut.ui.message

import android.os.Bundle
import android.view.View
import com.wegdut.wegdut.ui.BaseDaggerFragment
import com.wegdut.wegdut.ui.Reselectable
import javax.inject.Inject

abstract class SimpleMessageFragment<T>(layoutRes: Int) : BaseDaggerFragment(layoutRes),
    Reselectable {
    @Inject
    lateinit var messageView: SimpleMessageContract.View<T>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageView.bind(view)
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
        super.onDestroy()
        messageView.stop()
    }

    override fun reselect() {
        messageView.reselect()
    }
}