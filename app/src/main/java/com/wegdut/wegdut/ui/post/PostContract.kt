package com.wegdut.wegdut.ui.post

import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.scrollx.ScrollXPresenter
import com.wegdut.wegdut.scrollx.ScrollXView

class PostContract {
    abstract class View : ScrollXView<Post>()

    abstract class Presenter : ScrollXPresenter<Post, View>() {
        abstract fun toggleLike(post: Post, success: () -> Unit, error: (Throwable) -> Unit)
        abstract fun delete(post: Post, success: () -> Unit, error: (Throwable) -> Unit)
        abstract fun canSendPost(yes: () -> Unit, no: (Throwable) -> Unit)
    }
}