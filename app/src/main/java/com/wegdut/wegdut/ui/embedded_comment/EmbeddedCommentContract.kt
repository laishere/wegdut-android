package com.wegdut.wegdut.ui.embedded_comment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.comment.Comment
import com.wegdut.wegdut.data.comment.ComplexComment
import com.wegdut.wegdut.scrollx.ScrollXPresenter
import com.wegdut.wegdut.scrollx.ScrollXView
import com.wegdut.wegdut.ui.ImageDisplay

class EmbeddedCommentContract {
    abstract class View<T> : ScrollXView<ComplexComment>() {
        abstract fun setCommentCount(count: Int)
        abstract fun setLikeError(error: String)
        abstract fun setContent(content: T)
        abstract fun bind(
            view: android.view.View,
            activity: Activity,
            contentId: Long,
            contentType: ContentType,
            imageDisplay: ImageDisplay?
        )

        abstract fun showLoadingDialog()
        abstract fun setLoadingAction(action: String)
        abstract fun dismissLoadingDialog(success: Boolean, message: String)
        abstract fun setSendEnabled(enabled: Boolean)
        abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }

    abstract class Presenter<T> : ScrollXPresenter<ComplexComment, View<T>>() {
        var contentId = 0L
        lateinit var contentType: ContentType
        abstract fun subscribe(view: View<T>, id: Long, type: ContentType)
        abstract fun reply(parent: ComplexComment, text: String)
        abstract fun reply(parent: T, text: String, image: Uri?)
        abstract fun toggleLike(comment: Comment)
        abstract fun toggleLike(content: T)
        abstract fun loadMoreReplies(complexComment: ComplexComment)
    }
}