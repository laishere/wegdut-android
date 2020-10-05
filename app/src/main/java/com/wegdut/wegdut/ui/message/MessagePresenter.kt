package com.wegdut.wegdut.ui.message

import com.wegdut.wegdut.api.MessageApi
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.data.message.MessageUnread
import com.wegdut.wegdut.event.MessageAlreadyReadEvent
import com.wegdut.wegdut.utils.ApiUtils.extract
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class MessagePresenter @Inject constructor() : MessageContract.Presenter() {

    @Inject
    lateinit var messageApi: MessageApi
    private var unread: MessageUnread? = null

    override fun updateUnread() {
        launch {
            tryIt({
                unread = io {
                    messageApi.getMessageUnread().extract().toMessageUnread()
                }
                view?.setMessageUnread(unread!!)
            })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageAlreadyReadEvent(event: MessageAlreadyReadEvent) {
        unread?.run {
            when (event.type) {
                MessageType.LIKE -> like--
                MessageType.REPLY -> reply--
                MessageType.SYSTEM -> system--
            }
            view?.setMessageUnread(this)
        }
    }

    override fun subscribe(view: MessageContract.View) {
        super.subscribe(view)
        EventBus.getDefault().register(this)
    }

    override fun unsubscribe() {
        EventBus.getDefault().unregister(this)
        super.unsubscribe()
    }
}