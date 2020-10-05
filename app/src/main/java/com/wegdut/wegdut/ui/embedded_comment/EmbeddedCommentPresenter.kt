package com.wegdut.wegdut.ui.embedded_comment

import android.net.Uri
import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.comment.Comment
import com.wegdut.wegdut.data.comment.ComplexComment
import com.wegdut.wegdut.data.comment.PostCommentDto
import com.wegdut.wegdut.data.embedded_comment.EmbeddedCommentRepository
import com.wegdut.wegdut.data.remote_storage.StorageRepository
import javax.inject.Inject

open class EmbeddedCommentPresenter<T> @Inject constructor() :
    EmbeddedCommentContract.Presenter<T>() {
    @Inject
    lateinit var embeddedCommentRepository: EmbeddedCommentRepository<T>

    @Inject
    lateinit var storageRepository: StorageRepository

    private var isContentReady = false

    private fun getContent() {
        isContentReady = false
        refreshing = true
        launch {
            tryIt({
                val content = io {
                    embeddedCommentRepository.getContent()
                }
                view?.setContent(content)
                isContentReady = true
                loadingError = null
                update(0, 0)
            }) {
                MyLog.error(this, it)
                loadingError = MyException.handle(it)
                refreshing = false
            }
        }
    }

    override fun update(firstVisibleItem: Int, lastVisibleItem: Int) {
        if (!isContentReady) return
        super.update(firstVisibleItem, lastVisibleItem)
    }

    override fun refresh() {
        super.refresh()
        getContent()
    }

    override fun subscribe(view: EmbeddedCommentContract.View<T>, id: Long, type: ContentType) {
        contentId = id
        contentType = type
        embeddedCommentRepository.setContext(id, type)
        subscribe(view)
        getContent()
    }

    override fun reply(parent: ComplexComment, text: String) {
        launch {
            view?.showLoadingDialog()
            view?.setSendEnabled(false)
            tryIt({
                io { embeddedCommentRepository.canPostComment() }
                view?.setLoadingAction("正在发表")
                val dto = PostCommentDto(text, emptyList())
                val items = io { embeddedCommentRepository.reply(parent, dto) }
                view?.setItems(items)
                view?.dismissLoadingDialog(true, "已发表")
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.dismissLoadingDialog(false, err)
            }
            view?.setSendEnabled(true)
        }
    }

    override fun reply(parent: T, text: String, image: Uri?) {
        launch {
            view?.showLoadingDialog()
            view?.setSendEnabled(false)
            tryIt({
                io { embeddedCommentRepository.canPostComment() }
                val imageUrls = mutableListOf<String>()
                if (image != null) {
                    view?.setLoadingAction("正在上传图片")
                    val url = io { storageRepository.uploadImage(image) }
                    imageUrls.add(url)
                }
                view?.setLoadingAction("正在发表")
                val dto = PostCommentDto(text, imageUrls)
                val items = io { embeddedCommentRepository.reply(parent, dto) }
                view?.setContent(parent)
                view?.setItems(items)
                view?.dismissLoadingDialog(true, "已发表")
                onContentChanged(parent)
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.dismissLoadingDialog(false, err)
            }
            view?.setSendEnabled(true)
        }
    }

    override fun toggleLike(comment: Comment) {
        doToggleLike(comment, embeddedCommentRepository::toggleLike) {
            view?.setItems(it)
        }
    }

    override fun toggleLike(content: T) {
        doToggleLike(content, embeddedCommentRepository::toggleLike) {
            view?.setContent(content)
            onContentChanged(content)
        }
    }

    private fun <T, R> doToggleLike(item: T, toggleLike: (T) -> R, success: (R) -> Unit) {
        launch {
            tryIt({
                val items = io { toggleLike(item) }
                success(items)
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.setLikeError(err)
            }
        }
    }

    override fun loadMoreReplies(complexComment: ComplexComment) {
        launch {
            tryIt({
                val items = io {
                    embeddedCommentRepository.loadMoreReplies(complexComment)
                }
                view?.setItems(items)
            })
        }
    }

    protected open fun onContentChanged(content: T) {}
}