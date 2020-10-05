package com.wegdut.wegdut.ui.post

import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.data.post.PostRepository
import com.wegdut.wegdut.event.LoginEvent
import com.wegdut.wegdut.event.PostChangedEvent
import com.wegdut.wegdut.event.SendPostEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class PostPresenter @Inject constructor() : PostContract.Presenter() {

    @Inject
    lateinit var postRepository: PostRepository

    override fun toggleLike(post: Post, success: () -> Unit, error: (Throwable) -> Unit) {
        launch {
            tryIt({
                val items = io {
                    postRepository.toggleLike(post)
                }
                view?.setItems(items)
                success()
            }, errorHandler = error)
        }
    }

    override fun delete(post: Post, success: () -> Unit, error: (Throwable) -> Unit) {
        launch {
            tryIt({
                val items = io {
                    postRepository.delete(post)
                    postRepository.items
                }
                view?.setItems(items)
                success()
            }, errorHandler = error)
        }
    }

    override fun canSendPost(yes: () -> Unit, no: (Throwable) -> Unit) {
        launch {
            tryIt({
                io {
                    postRepository.canSendPost()
                }
                yes()
            }, errorHandler = no)
        }
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
    fun onSendBlogEvent(event: SendPostEvent) {
        updateTop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        updateAll()
    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onUserModificationEvent(event: UserModificationEvent) {
//        updateAll()
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPostChangeEvent(event: PostChangedEvent) {
        launch {
            tryIt({
                val items = io {
                    postRepository.update(event.post)
                    postRepository.items
                }
                view?.setItems(items)
            })
        }
    }
}