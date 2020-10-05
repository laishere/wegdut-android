package com.wegdut.wegdut.ui.message.system

import android.view.View
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.data.message.system.SystemMessage
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.ui.message.SimpleMessageView
import javax.inject.Inject

class SystemMessageView @Inject constructor() : SimpleMessageView<SystemMessage>() {
    override val type: MessageType = MessageType.SYSTEM

    override fun bind(view: View) {
        super.bind(view)
        adapter.onItemClickListener = object : ListRVAdapter.OnItemClickListener {
            override fun onClick(view: View, pos: Int) {
                val data = adapter.items!![pos].data
                if (data !is SystemMessage) return
                if (data.hasRead) return
                messagePresenter.read(data)
            }
        }
    }
}