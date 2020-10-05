package com.wegdut.wegdut.dialog

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.wegdut.wegdut.R

class LoadingDialog(private val context: Context) {
    private lateinit var dialog: Dialog
    private var actionTextView: TextView? = null
    private var icon: ImageView? = null
    private var progressBar: View? = null
    var action: CharSequence? = null
        set(value) {
            field = value
            Handler(context.mainLooper).post {
                if (value == null) {
                    actionTextView?.visibility = View.GONE
                    return@post
                }
                actionTextView?.visibility = View.VISIBLE
                actionTextView?.text = value
            }
        }

    fun show() {
        Handler(context.mainLooper).post {
            if (!this::dialog.isInitialized) {
                initDialog()
                return@post
            }
            reset()
            dialog.show()
        }
    }

    fun dismiss() {
        if (this::dialog.isInitialized)
            dialog.dismiss()
    }

    private fun reset() {
        progressBar?.visibility = View.VISIBLE
        icon?.visibility = View.INVISIBLE
        icon?.isHovered = false
        icon?.isSelected = false
        actionTextView?.isHovered = false
        actionTextView?.isSelected = false
    }

    fun successAndDismiss(text: String, delay: Long = 1500L, afterHide: () -> Unit = {}) {
        action = text
        icon?.setImageResource(R.drawable.ic_check_circle)
        icon?.isHovered = false
        icon?.isActivated = true
        icon?.visibility = View.VISIBLE
        progressBar?.visibility = View.INVISIBLE
        actionTextView?.isHovered = false
        actionTextView?.isActivated = true
        Handler(context.mainLooper).postDelayed({
            dismiss()
            afterHide()
        }, delay)
    }

    fun errorAndDismiss(text: String, delay: Long = 1500L, afterHide: () -> Unit = {}) {
        action = text
        icon?.setImageResource(R.drawable.ic_error)
        icon?.isHovered = true
        icon?.isActivated = false
        icon?.visibility = View.VISIBLE
        progressBar?.visibility = View.INVISIBLE
        actionTextView?.isHovered = true
        actionTextView?.isActivated = false
        Handler(context.mainLooper).postDelayed({
            dismiss()
            afterHide()
        }, delay)
    }

    private fun initDialog() {
        dialog = Dialog(context, R.style.SimpleDialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.show()
        actionTextView = dialog.findViewById(R.id.action)
        icon = dialog.findViewById(R.id.icon)
        progressBar = dialog.findViewById(R.id.progress_bar)
        reset()
    }

}