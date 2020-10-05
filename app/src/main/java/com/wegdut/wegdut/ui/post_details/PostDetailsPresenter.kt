package com.wegdut.wegdut.ui.post_details

import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.event.PostChangedEvent
import com.wegdut.wegdut.ui.embedded_comment.EmbeddedCommentPresenter
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class PostDetailsPresenter @Inject constructor() : EmbeddedCommentPresenter<Post>() {
    override fun onContentChanged(content: Post) {
        EventBus.getDefault().post(PostChangedEvent(content))
    }
}