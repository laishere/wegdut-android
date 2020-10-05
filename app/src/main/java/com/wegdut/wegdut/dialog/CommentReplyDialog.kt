package com.wegdut.wegdut.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wegdut.wegdut.R
import com.wegdut.wegdut.config.Config
import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.glide.GlideApp
import com.wegdut.wegdut.utils.GlideUtils
import com.wegdut.wegdut.utils.PermissionUtils
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy

class CommentReplyDialog(private val activity: Activity) {
    private var postCommentDialog: BottomSheetDialog =
        BottomSheetDialog(activity, R.style.BottomInputDialog)
    private val replyIconView: ImageView
    private val replyAbstractView: TextView
    private val replyContextWrapper: View
    private val replyInput: EditText
    private val sendBtn: ImageButton
    private val pickImageButton: View
    private val imageWrapper: View
    private val imageView: ImageView
    private val deleteImageButton: View
    private var replyIcon: String? = null
    private var replyAbstract: CharSequence? = null
    private var replyDialogListener: ReplyDialogListener? = null
    private var image: Uri? = null
        set(value) {
            field = value
            updateImage()
        }
    private val pickImageCode = 1729
    var isImageAllow = false
        private set

    init {
        postCommentDialog.setContentView(R.layout.dialog_post_comment)
        replyIconView = postCommentDialog.findViewById(R.id.icon)!!
        replyAbstractView = postCommentDialog.findViewById(R.id.context_abstract)!!
        replyContextWrapper = postCommentDialog.findViewById(R.id.context_wrapper)!!
        replyInput = postCommentDialog.findViewById(R.id.input)!!
        sendBtn = postCommentDialog.findViewById(R.id.btn_send)!!
        pickImageButton = postCommentDialog.findViewById(R.id.btn_pick_image)!!
        imageWrapper = postCommentDialog.findViewById(R.id.img_wrapper)!!
        imageView = postCommentDialog.findViewById(R.id.img)!!
        deleteImageButton = postCommentDialog.findViewById(R.id.btn_delete_img)!!
        sendBtn.setOnClickListener {
            val text = replyInput.text
            replyDialogListener?.onSend(text, image)
        }
        pickImageButton.setOnClickListener {
            pickImage()
        }
        deleteImageButton.setOnClickListener {
            image = null
        }
        setupReplyTouch()
    }

    private fun pickImage() {
        PermissionUtils.pickImage(activity, "需要存储权限哦") {
            Matisse.from(activity)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .imageEngine(GlideEngine())
                .capture(true)
                .captureStrategy(CaptureStrategy(false, Config.fileProviderAuthorities))
                .forResult(pickImageCode)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupReplyTouch() {
        replyInput.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> v.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    private fun updateImage() {
        val hasImage = image != null
        pickImageButton.isEnabled = !hasImage
        imageWrapper.visibility =
            if (hasImage) View.VISIBLE
            else View.GONE
        if (hasImage) {
            GlideApp.with(imageView)
                .load(image)
                .into(imageView)
        }
    }

    fun allowPickImage(allow: Boolean) {
        isImageAllow = allow
        pickImageButton.visibility = if (allow) View.VISIBLE else View.GONE
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!isImageAllow) return
        if (resultCode != Activity.RESULT_OK || requestCode != pickImageCode) return
        image = Matisse.obtainResult(data).firstOrNull()
    }

    fun show() {
        if (replyIcon != null) {
            GlideUtils.load(replyIconView, ImageItem(replyIcon!!))
                .placeholder(R.drawable.shape_rect_image_placeholder)
                .into(replyIconView)
            replyAbstractView.text = replyAbstract
            replyContextWrapper.visibility = View.VISIBLE
        } else replyContextWrapper.visibility = View.GONE
        replyInput.requestFocusFromTouch()
        updateImage()
        postCommentDialog.show()
    }

    fun dismiss() {
        postCommentDialog.dismiss()
    }

    fun setSendEnabled(enabled: Boolean) {
        sendBtn.isEnabled = enabled
    }

    fun setHint(hint: CharSequence?) {
        replyInput.hint = hint
    }

    fun setReplyIcon(icon: String?) {
        replyIcon = icon
    }

    fun setReplyAbstract(abstract: CharSequence?) {
        replyAbstract = abstract
    }

    fun clearInput() {
        replyInput.text.clear()
        image = null
    }

    fun setReplyDialogListener(onSend: (CharSequence) -> Unit) {
        this.replyDialogListener = object :
            ReplyDialogListener {
            override fun onSend(text: CharSequence, image: Uri?) {
                onSend(text)
            }
        }
    }

    fun setReplyDialogListener(onSend: (CharSequence, Uri?) -> Unit) {
        this.replyDialogListener = object :
            ReplyDialogListener {
            override fun onSend(text: CharSequence, image: Uri?) {
                onSend(text, image)
            }
        }
    }

    interface ReplyDialogListener {
        fun onSend(text: CharSequence, image: Uri?)
    }
}