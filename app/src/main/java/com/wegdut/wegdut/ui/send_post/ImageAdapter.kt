package com.wegdut.wegdut.ui.send_post

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.BaseTypeRVAdapter

class ImageAdapter : BaseTypeRVAdapter() {

    class ImageViewHolder(itemView: View) : BaseTypeRVAdapter.ViewHolder(itemView) {
        private val img: ImageView = itemView.findViewById(R.id.img)
        override fun bind(data: Item) {
            Glide.with(img)
                .load(data.data as Uri)
                .placeholder(R.drawable.shape_rect_image_placeholder)
                .centerCrop()
                .into(img)
        }
    }

    class AddImageViewHolder(itemView: View) : BaseTypeRVAdapter.ViewHolder(itemView) {
        override fun bind(data: Item) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflate(parent, viewType)
        return when (viewType) {
            R.layout.item_square_image -> ImageViewHolder(view)
            else -> AddImageViewHolder(view)
        }
    }
}