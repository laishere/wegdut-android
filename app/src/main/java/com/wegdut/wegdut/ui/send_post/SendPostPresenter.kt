package com.wegdut.wegdut.ui.send_post

import android.net.Uri
import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.data.remote_storage.StorageRepository
import com.wegdut.wegdut.data.send_post.SendPostDto
import com.wegdut.wegdut.data.send_post.SendPostRepository
import com.wegdut.wegdut.event.SendPostEvent
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class SendPostPresenter @Inject constructor() : SendPostContract.Presenter() {
    @Inject
    lateinit var sendPostRepository: SendPostRepository

    @Inject
    lateinit var storageRepository: StorageRepository

    override fun sendPost(text: String, images: List<Uri>) {
        view?.setSendEnabled(false)
        view?.showLoadingDialog()
        launch {
            tryIt({
                view?.setAction("正在处理")
                io { sendPostRepository.canSendPost() }
                view?.showLoadingDialog()
                val imageUrls = mutableListOf<String>()
                if (images.isNotEmpty()) {
                    for (i in images.withIndex()) {
                        view?.setAction("正在上传图片 ${i.index + 1} / ${images.size}")
                        imageUrls.add(io {
                            storageRepository.uploadImage(i.value)
                        })
                    }
                }
                view?.setAction("正在发表")
                val dto = SendPostDto(text, imageUrls)
                val post = io {
                    sendPostRepository.sendPost(dto)
                }
                view?.dismissLoadingDialog(true, "已发表")
                EventBus.getDefault().post(SendPostEvent(post))
            }) {
                MyLog.error(this, it)
                val err = MyException.handle(it)
                view?.dismissLoadingDialog(false, err)
            }
            view?.setSendEnabled(true)
        }
    }
}