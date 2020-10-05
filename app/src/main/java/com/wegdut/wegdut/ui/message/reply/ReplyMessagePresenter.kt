package com.wegdut.wegdut.ui.message.reply

import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.comment.PostCommentDto
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.data.message.reply.ReplyMessage
import com.wegdut.wegdut.data.message.reply.ReplyMessageRepository
import com.wegdut.wegdut.event.LoginEvent
import com.wegdut.wegdut.event.MessageAlreadyReadEvent
import com.wegdut.wegdut.event.UserModificationEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class ReplyMessagePresenter @Inject constructor() : ReplyMessageContract.Presenter() {
    @Inject
    lateinit var replyMessageRepository: ReplyMessageRepository
    private lateinit var type: MessageType
    override var clearItemsAfterRefreshingFailed = true

    override fun reply(item: ReplyMessage, text: String) {
        launch {
            view?.showLoadingDialog()
            tryIt({
                val items = io {
                    replyMessageRepository.reply(item, PostCommentDto(text, emptyList()))
                }
                view?.setItems(items)
                view?.dismissLoadingDialog(true, "已回复")
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.dismissLoadingDialog(false, err)
            }
        }
    }

    override fun toggleLike(item: ReplyMessage) {
        launch {
            tryIt({
                val items = io {
                    replyMessageRepository.toggleLike(item)
                }
                view?.setItems(items)
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setLikeError(err)
            }
        }
    }

    override fun read(item: ReplyMessage) {
        launch {
            tryIt({
                val items = io {
                    replyMessageRepository.read(item)
                }
                view?.setItems(items)
                EventBus.getDefault().post(MessageAlreadyReadEvent(type))
            })
        }
    }

    override fun subscribe(view: ReplyMessageContract.View) {
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