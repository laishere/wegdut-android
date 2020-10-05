package com.wegdut.wegdut.ui.message.reply

import android.app.Activity
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.data.message.reply.ReplyMessage
import com.wegdut.wegdut.scroll.ScrollPresenter
import com.wegdut.wegdut.scroll.ScrollView
import com.wegdut.wegdut.ui.Reselectable

class ReplyMessageContract {
    abstract class View : ScrollView<ReplyMessage>(), Reselectable {
        abstract val type: MessageType
        abstract fun showLoadingDialog()
        abstract fun setLoadingAction(action: String)
        abstract fun dismissLoadingDialog(success: Boolean, message: String)
        abstract fun setLikeError(error: String)
        abstract fun bind(view: android.view.View, activity: Activity)
    }

    abstract class Presenter : ScrollPresenter<ReplyMessage, View>() {
        abstract fun reply(item: ReplyMessage, text: String)
        abstract fun toggleLike(item: ReplyMessage)
        abstract fun read(item: ReplyMessage)
    }
}