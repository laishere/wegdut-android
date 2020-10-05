package com.wegdut.wegdut.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.utils.GlideUtils

abstract class GridImageAdapter : ListRVAdapter<ImageItem?, GridImageAdapter.ViewHolder>() {

    override var items: List<ImageItem?>? = null
        set(value) {
            field = transform(value)
            notifyDataSetChanged()
        }

    abstract fun transform(items: List<ImageItem?>?): List<ImageItem?>?

    class ViewHolder(itemView: View) : ListRVAdapter.ViewHolder<ImageItem?>(itemView) {
        override fun bind(data: ImageItem?) {
            itemView.isClickable = data != null
            itemView.isFocusable = itemView.isClickable
            if (data == null) return
            val img = itemView as ImageView
            GlideUtils.load(img, data, sizeLimit = 360)
                .centerCrop().into(img)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (items!![position] == null) return 0
        return 1
    }

    abstract fun getImageView(parent: ViewGroup): ImageView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            if (viewType == 1) getImageView(parent)
            else View(parent.context)
        return ViewHolder(view)
    }
}