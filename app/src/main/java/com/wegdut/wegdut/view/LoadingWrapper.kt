package com.wegdut.wegdut.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import com.wegdut.wegdut.R

class LoadingWrapper(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private val loadingProgressBar: ContentLoadingProgressBar
    private val errorTextView: TextView
    private val emptyView: View

    var empty: Boolean = false
        set(value) {
            field = value
            emptyView.visibility = if (empty) View.VISIBLE else View.GONE
        }

    var loading: Boolean = true
        set(value) {
            field = value
            if (loading) loadingProgressBar.show()
            else loadingProgressBar.hide()
        }

    var error: String? = null
        set(value) {
            field = value
            if (value != null) {
                errorTextView.text = value
                errorTextView.visibility = View.VISIBLE
            } else errorTextView.visibility = View.GONE
        }

    init {
        val inflater = LayoutInflater.from(context)
        loadingProgressBar = inflater.inflate(
            R.layout.loading_wrapper_loading_indicator,
            this,
            false
        ) as ContentLoadingProgressBar
        errorTextView =
            inflater.inflate(R.layout.loading_wrapper_error_text, this, false) as TextView
        emptyView = inflater.inflate(R.layout.loading_wrapper_empty, this, false)
        emptyView.visibility = View.GONE
        addView(loadingProgressBar)
        addView(errorTextView)
        addView(emptyView)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
        bringChildToFront(errorTextView)
        bringChildToFront(loadingProgressBar)
    }

}