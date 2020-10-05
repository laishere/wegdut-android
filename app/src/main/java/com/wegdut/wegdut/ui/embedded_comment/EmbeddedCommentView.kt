package com.wegdut.wegdut.ui.embedded_comment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.comment.Comment
import com.wegdut.wegdut.data.comment.ComplexComment
import com.wegdut.wegdut.dialog.CommentReplyDialog
import com.wegdut.wegdut.dialog.LoadingDialog
import com.wegdut.wegdut.ui.BaseTypeRVAdapter
import com.wegdut.wegdut.ui.ImageDisplay
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.utils.MessageUtils
import com.wegdut.wegdut.view.FakeInput
import com.wegdut.wegdut.view.ShineButton
import javax.inject.Inject

abstract class EmbeddedCommentView<T> : EmbeddedCommentContract.View<T>() {

    @Inject
    lateinit var embeddedCommentAdapter: EmbeddedCommentAdapter<T>

    @Inject
    lateinit var embeddedCommentPresenter: EmbeddedCommentContract.Presenter<T>

    private lateinit var activity: Activity
    protected lateinit var commentReplyDialog: CommentReplyDialog
    private lateinit var loadingDialog: LoadingDialog
    private var content: T? = null
    protected var replyCount = 0
    protected var iReply = false
    protected var hasReplyAnimation = false
    protected var likeCount = 0
    protected var iLike = false
    protected var hasLikeAnimation = false
    private var replyId = 0L
    private var replyType: ContentType? = null
    private var contentId: Long = 0L
    private lateinit var contentType: ContentType
    private lateinit var fakeInput: FakeInput
    private lateinit var likeBtn: ShineButton
    private lateinit var likeTextView: TextView
    private lateinit var replyBtn: ShineButton
    private lateinit var replyTextView: TextView
    private var parentComment: ComplexComment? = null

    override fun setCommentCount(count: Int) {
        replyCount = count
        lazyRVUpdateHelper.update()
    }

    override fun setLikeError(error: String) {
        MessageUtils.info(activity, "点赞失败：$error")
    }

    override fun setContent(content: T) {
        this.content = content
        setContentInfo(content)
        resetReply()
        likeTextView.text = "$likeCount"
        likeTextView.isActivated = iLike
        replyTextView.text = "$replyCount"
        replyTextView.isActivated = iReply
        likeBtn.setState(ShineButton.ShineButtonState(iLike, "", hasLikeAnimation))
        replyBtn.setState(ShineButton.ShineButtonState(iReply, "", hasReplyAnimation))
        lazyRVUpdateHelper.update()
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
        } else
            loadingDialog.errorAndDismiss(message)
    }

    override fun setSendEnabled(enabled: Boolean) {
        commentReplyDialog.setSendEnabled(enabled)
    }

    override fun onMerge(list: MutableList<BaseTypeRVAdapter.Item>) {
        content?.let {
            val item = embeddedCommentAdapter.getContentItem(it)
            list.add(item)
            list.add(
                BaseTypeRVAdapter.Item(
                    1,
                    R.layout.item_embeded_comment_tab_view,
                    replyCount
                )
            )
        }
        super.onMerge(list)
    }

    override fun bind(
        view: View,
        activity: Activity,
        contentId: Long,
        contentType: ContentType,
        imageDisplay: ImageDisplay?
    ) {
        this.activity = activity
        this.contentId = contentId
        this.contentType = contentType
        embeddedCommentAdapter.imageDisplay = imageDisplay
        commentReplyDialog = CommentReplyDialog(activity)
        loadingDialog = LoadingDialog(activity)
        fakeInput = view.findViewById(R.id.comment_fake_input)
        likeBtn = view.findViewById(R.id.btn_like)
        likeTextView = view.findViewById(R.id.btn_like_text)
        replyBtn = view.findViewById(R.id.btn_reply)
        replyTextView = view.findViewById(R.id.btn_reply_text)
        view.findViewById<View>(R.id.btn_like_wrapper).setOnClickListener {
            content?.let { embeddedCommentPresenter.toggleLike(it) }
        }
        view.findViewById<View>(R.id.btn_reply_wrapper).setOnClickListener {
            content?.let {
                replyContent()
            }
        }
        fakeInput.setOnClickListener {
            if (replyId > 0)
                commentReplyDialog.show()
        }
        adapter.onItemClickListener = object : ListRVAdapter.OnItemClickListener {
            override fun onClick(view: View, pos: Int) {
                val item = adapter.items?.get(pos) ?: return
                val data = item.data
                if (data is ComplexComment)
                    replyComment(data.comment, data)
                else if (data == content)
                    replyContent()
            }
        }
        embeddedCommentAdapter.commentListener = commentListener
        commentReplyDialog.setReplyDialogListener(this::postComment)
        bind(view)
    }

    override fun onStart() {
        embeddedCommentPresenter.subscribe(this, contentId, contentType)
    }

    private fun postComment(text: CharSequence, image: Uri?) {
        when {
            commentReplyDialog.isImageAllow
                    && text.isBlank()
                    && image == null ->
                MessageUtils.info(activity, "请输入内容或选择图片")
            !commentReplyDialog.isImageAllow && text.isBlank() -> MessageUtils.info(
                activity,
                "请输入内容"
            )
            else -> {
                if (replyType == contentType)
                    embeddedCommentPresenter.reply(content!!, text.toString(), image)
                else
                    embeddedCommentPresenter.reply(parentComment!!, text.toString())
            }
        }
    }

    private val commentListener = object : EmbeddedCommentAdapter.CommentListener {
        override fun onLike(comment: Comment) {
            embeddedCommentPresenter.toggleLike(comment)
        }

        override fun onClickReply(parent: ComplexComment, comment: Comment) {
            replyComment(comment, parent)
        }

        override fun onClickShowMore(complexComment: ComplexComment) {
            embeddedCommentPresenter.loadMoreReplies(complexComment)
        }
    }

    private fun replyComment(comment: Comment, parent: ComplexComment) {
        parentComment = parent
        val u = comment.user
        applyReplyInfo(comment.id, ContentType.COMMENT)
        val hint = "回复 ${u.nickname}"
        fakeInput.text = hint
        commentReplyDialog.setReplyIcon(u.icon)
        commentReplyDialog.setReplyAbstract(comment.getAbstract())
        commentReplyDialog.setHint(hint)
        commentReplyDialog.allowPickImage(false)
        commentReplyDialog.show()
    }

    private fun resetReply() {
        applyReplyInfo(contentId, contentType)
        setContentReplyInfo(content!!)
        commentReplyDialog.allowPickImage(true)
    }

    private fun replyContent() {
        resetReply()
        commentReplyDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        commentReplyDialog.onActivityResult(requestCode, resultCode, data)
    }

    private fun applyReplyInfo(id: Long, type: ContentType) {
        if (id == replyId && type == replyType) return
        replyId = id
        replyType = type
        commentReplyDialog.clearInput()
        if (type != ContentType.COMMENT)
            parentComment = null
    }

    abstract fun setContentInfo(content: T)
    abstract fun setContentReplyInfo(content: T)
}