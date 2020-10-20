package com.wegdut.wegdut.ui.photo_view

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.doOnPreDraw
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.ListRVAdapter

class PhotoAdapter(
    private val selected: Int = 0,
    private val onPreDrawListener: ViewTreeObserver.OnPreDrawListener
) : ListRVAdapter<String, PhotoAdapter.ViewHolder>() {

    var onColorReadyListener: OnColorReadyListener? = null
    var onClickListener: View.OnClickListener? = null
    private val onClickListener2 = View.OnClickListener { onClickListener?.onClick(it) }
    lateinit var onBindListener: OnBindListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_photo_view_item, parent, false)
        return ViewHolder(view, onClickListener2)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onColorReadyListener = onColorReadyListener
        holder.onPreDrawListener = onPreDrawListener
        holder.onBindListener = onBindListener
        holder.selected = position == selected
        super.onBindViewHolder(holder, position)
    }

    class ViewHolder(itemView: View, onClickListener: View.OnClickListener) :
        ListRVAdapter.ViewHolder<String>(itemView) {
        private val photoView: PhotoView = itemView.findViewById(R.id.photo_view)
        var onColorReadyListener: OnColorReadyListener? = null
        var selected = false
        lateinit var onPreDrawListener: ViewTreeObserver.OnPreDrawListener
        lateinit var onBindListener: OnBindListener

        init {
            photoView.setOnClickListener(onClickListener)
        }

        override fun bind(data: String) {
            onBindListener.onBind(photoView, adapterPosition)
            Glide.with(photoView)
                .load(data)
                .override(Target.SIZE_ORIGINAL)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (resource is BitmapDrawable) {
                            Palette.from(resource.bitmap).generate {
                                if (it != null) {
                                    onColorReadyListener?.onReady(
                                        adapterPosition,
                                        it.getDarkMutedColor(Color.BLACK)
                                    )
                                }
                            }
                        }
                        itemView.doOnPreDraw {
                            if (selected) onPreDrawListener.onPreDraw()
                        }
                        return false
                    }

                })
                .into(photoView)
        }
    }

    interface OnColorReadyListener {
        fun onReady(position: Int, color: Int)
    }

    interface OnBindListener {
        fun onBind(view: View, position: Int)
    }
}