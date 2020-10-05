package com.wegdut.wegdut.ui.post

import android.view.ViewGroup
import android.widget.ImageView
import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.ui.GridImageAdapter
import com.wegdut.wegdut.view.SquareImageView

class GridImageAdapter3X3 : GridImageAdapter() {
    private var realSize = 0
    override fun transform(items: List<ImageItem?>?): List<ImageItem?>? {
        realSize = items?.size ?: 0
        if (items == null) return null
        if (items.size == 4) return listOf(items[0], items[1], null, items[2], items[3])
        return items
    }

    fun getRealPosition(pos: Int): Int {
        if (realSize == 4) {
            if (pos == 2) return -1
            else if (pos > 2) return pos - 1
        }
        return pos
    }

    override fun getImageView(parent: ViewGroup): ImageView {
        val img = SquareImageView(parent.context, null)
        img.scaleType = ImageView.ScaleType.CENTER_CROP
        return img
    }
}