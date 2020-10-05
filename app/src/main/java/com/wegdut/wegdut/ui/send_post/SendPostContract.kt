package com.wegdut.wegdut.ui.send_post

import android.net.Uri
import com.wegdut.wegdut.ui.BasePresenter

class SendPostContract {
    interface View {
        fun showLoadingDialog()
        fun dismissLoadingDialog(success: Boolean, message: String)
        fun setAction(action: String)
        fun setSendEnabled(enabled: Boolean)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun sendPost(text: String, images: List<Uri>)
    }
}