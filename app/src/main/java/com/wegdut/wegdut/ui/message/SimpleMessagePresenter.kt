package com.wegdut.wegdut.ui.message

import com.wegdut.wegdut.data.message.MessageRepository
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.event.LoginEvent
import com.wegdut.wegdut.event.MessageAlreadyReadEvent
import com.wegdut.wegdut.event.UserModificationEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

open class SimpleMessagePresenter<T> @Inject constructor() : SimpleMessageContract.Presenter<T>() {

    @Inject
    lateinit var messageRepository: MessageRepository<T>
    private lateinit var type: MessageType
    override var clearItemsAfterRefreshingFailed = true

    override fun read(item: T) {
        launch {
            tryIt({
                val items = io {
                    messageRepository.read(item)
                }
                view?.setItems(items)
                EventBus.getDefault().post(MessageAlreadyReadEvent(type))
            })
        }
    }

    override fun subscribe(view: SimpleMessageContract.View<T>) {
        super.subscribe(view)
        type = view.type
    }

    override fun start() {
        super.start()
        EventBus.getDefault().register(this)
    }

    override fun stop() {
        EventBus.getDefault().unregister(this)
        super.stop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        if (event.status == LoginEvent.Status.LOGIN)
            refresh()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUserModificationEvent(event: UserModificationEvent) {
        refresh()
    }
}