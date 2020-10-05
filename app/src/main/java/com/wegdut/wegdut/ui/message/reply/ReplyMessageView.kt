package com.wegdut.wegdut.ui.message.reply

import android.app.Activity
import android.view.View
import com.wegdut.wegdut.data.message.MessageType
import com.wegdut.wegdut.data.message.reply.ReplyMessage
import com.wegdut.wegdut.dialog.CommentReplyDialog
import com.wegdut.wegdut.dialog.LoadingDialog
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.utils.MessageUtils
import javax.inject.Inject

class ReplyMessageView @Inject constructor() : ReplyMessageContract.View() {
    @Inject
    lateinit var replyMessagePresenter: ReplyMessageContract.Presenter

    override val type: MessageType = MessageType.REPLY
    lateinit var commentReplyDialog: CommentReplyDialog
    lateinit var loadingDialog: LoadingDialog
    lateinit var activity: Activity

    override fun bind(view: View, activity: Activity) {
        bind(view)
        commentReplyDialog = CommentReplyDialog(activity)
        loadingDialog = LoadingDialog(activity)
        this.activity = activity
        (adapter as ReplyAdapter).replyViewHolderCallback = replyCallback
    }

    override fun bind(view: View) {
        super.bind(view)
        adapter.onItemClickListener = object : ListRVAdapter.OnItemClickListener {
            override fun onClick(view: View, pos: Int) {
                val data = adapter.items!![pos].data
                if (data !is ReplyMessage) return
                if (data.hasRead) return
                replyMessagePresenter.read(data)
            }
        }
    }

    private val replyCallback = object : ReplyAdapter.ReplyViewHolderCallback {
        override fun reply(item: ReplyMessage) {
            item.comment.run {
                commentReplyDialog.setHint("回复 ${user.nickname}")
                commentReplyDialog.setReplyAbstract(getAbstract())
                commentReplyDialog.setReplyIcon(user.icon)
            }
            commentReplyDialog.allowPickImage(false)
            commentReplyDialog.setReplyDialogListener { text, _ ->
                if (text.isBlank()) {
                    MessageUtils.info(activity, "请输入内容")
                    return@setReplyDialogListener
                }
                replyMessagePresenter.reply(item, text.toString())
            }
            commentReplyDialog.show()
        }

        override fun like(item: ReplyMessage) {
            replyMessagePresenter.toggleLike(item)
        }

    }

    override fun showLoadingDialog() {
        loadingDialog.show()
    }

    override fun setLoadingAction(action: String) {
        loadingDialog.action = action
    }

    override fun dismissLoadingDialog(success: Boolean, message: String) {
        if (success) {
            loadingDialog.successAndDismiss(message)
            commentReplyDialog.clearInput()
            commentReplyDialog.dismiss()
        } else loadingDialog.errorAndDismiss(message)
    }

    override fun setLikeError(error: String) {
        MessageUtils.info(activity, error)
    }

    override fun reselect() {
        recyclerView.scrollToPosition(0)
    }
}