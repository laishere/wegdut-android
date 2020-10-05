package com.wegdut.wegdut.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.SharedElementCallback
import androidx.core.view.ViewCompat
import com.wegdut.wegdut.ui.photo_view.PhotoViewActivity

open class ImageDisplay(private val activity: Activity) {
    fun showImage(image: String, view: View? = null) {
        val views = if (view != null) listOf(view) else emptyList()
        showImages(arrayOf(image), 0, views)
    }

    fun showImages(
        images: Array<String>,
        selected: Int,
        views: List<View>,
        noExitTransition: Boolean = false
    ) {
        val intent = Intent(activity, PhotoViewActivity::class.java)
        intent.putExtra(PhotoViewActivity.IMAGES, images)
        intent.putExtra(PhotoViewActivity.SELECTED_IMAGE, selected)
        intent.putExtra(PhotoViewActivity.NO_EXIT_TRANSITION, noExitTransition)
        if (views.isEmpty())
            ActivityCompat.startActivity(activity, intent, null)
        else {
            val view = views[selected]
            val transitionName = PhotoViewActivity.getTransitionName(selected)
            ViewCompat.setTransitionName(view, transitionName)
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, transitionName)
            ActivityCompat.setExitSharedElementCallback(activity, object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: MutableList<String>?,
                    sharedElements: MutableMap<String, View>?
                ) {
                    if (names == null || sharedElements == null) return
                    val name = names.firstOrNull() ?: return
                    val index = PhotoViewActivity.getIndexByTransitionName(name)
                    sharedElements[name] = views[index]
                }

                override fun onSharedElementEnd(
                    sharedElementNames: MutableList<String>?,
                    sharedElements: MutableList<View>?,
                    sharedElementSnapshots: MutableList<View>?
                ) {
                    ActivityCompat.setExitSharedElementCallback(activity, null)
                    onSharedElementEnd()
                }
            })
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
        onImageShow()
    }

    protected open fun onImageShow() {}
    protected open fun onSharedElementEnd() {}
}