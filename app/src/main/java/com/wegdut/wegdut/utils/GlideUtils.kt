package com.wegdut.wegdut.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.drawable.MyColorDrawable
import com.wegdut.wegdut.glide.GlideApp
import com.wegdut.wegdut.glide.GlideRequest
import com.wegdut.wegdut.ui.ViewHolderHelper
import kotlin.math.roundToInt
import kotlin.math.sqrt

object GlideUtils {
    /**
     * @param sizeLimit 指最大尺寸width x height不超过sizeLimit的平方，为-1则不修改
     * @param resize 为true时，如果img不具有合法尺寸，则加载原图
     */
    fun load(
        view: View,
        img: ImageItem,
        saveState: Boolean = false,
        sizeLimit: Int = -1,
        resize: Boolean = true
    ): GlideRequest<Drawable> {
        var g = GlideApp.with(view).load(img.url)
        if (img.width > 0 && img.height > 0) {
            var resizeW = img.width
            var resizeH = img.height
            if (sizeLimit > 0) {
                var w = img.width
                var h = img.height
                val size = img.width * img.height
                val sizeLimit2 = sizeLimit * sizeLimit
                if (size > sizeLimit2) {
                    val ratio = sqrt(sizeLimit2.toFloat() / size)
                    w = (w * ratio).roundToInt()
                    h = (h * ratio).roundToInt()
                }
                resizeW = w
                resizeH = h
            }
            var c =
                if (img.color != null) Color.parseColor(img.color)
                else Color.WHITE
            if (c == Color.WHITE)
                c = ResourcesCompat.getColor(view.resources, R.color.grey, null)
            val d = MyColorDrawable(c, resizeW, resizeH)
            val factory = DrawableCrossFadeFactory
                .Builder()
                .setCrossFadeEnabled(true)
                .build()
            g = g.placeholder(d)
                .transition(DrawableTransitionOptions.withCrossFade(factory))
                .override(resizeW, resizeH)
        } else if (resize) {
            g = g.override(Target.SIZE_ORIGINAL)
        }
        if (saveState) {
            g = g.addListener(object : RequestListener<Drawable> {
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
                    ViewHolderHelper.save(view, img)
                    return false
                }
            })
        }
        return g
    }

    fun loadIcon(iconView: ImageView, icon: String, saveState: Boolean = false) {
        load(iconView, ImageItem(icon), saveState, resize = false)
            .placeholder(R.drawable.ic_default_user_icon)
            .into(iconView)
    }
}