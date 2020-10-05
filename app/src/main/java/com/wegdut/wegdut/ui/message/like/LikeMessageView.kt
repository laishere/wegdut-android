package com.wegdut.wegdut.ui.message.like

import android.view.View
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.data.message.like.LikeMessage
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.ui.message.SimpleMessageView
import javax.inject.Inject

class LikeMessageView @Inject constructor() : SimpleMessageView<LikeMessage>() {
    override val type: MessageType = MessageType.LIKE

    override fun bind(view: View) {
        super.bind(view)
        adapter.onItemClickListener = object : ListRVAdapter.OnItemClickListener {
            override fun onClick(view: View, pos: Int) {
                val data = adapter.items!![pos].data
                if (data !is LikeMessage) return
                if (data.hasRead) return
                messagePresenter.read(data)
            }
        }
    }
}