package com.wegdut.wegdut.ui.post_details

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.ui.GridSpacingItemDecoration
import com.wegdut.wegdut.ui.ImageDisplay
import com.wegdut.wegdut.ui.ViewHolderHelper
import com.wegdut.wegdut.ui.embedded_comment.EmbeddedCommentAdapter
import com.wegdut.wegdut.ui.post.GridImageAdapter3X3
import com.wegdut.wegdut.utils.DateUtils
import com.wegdut.wegdut.utils.GlideUtils
import com.wegdut.wegdut.utils.UIUtils
import javax.inject.Inject

class PostDetailsAdapter @Inject constructor() : EmbeddedCommentAdapter<Post>() {

    override fun getViewHolder(viewType: Int, view: View): ViewHolder? {
        return if (viewType == R.layout.item_post_details) BlogViewHolder(view)
        else super.getViewHolder(viewType, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is BlogViewHolder) holder.imageDisplay = imageDisplay
        super.onBindViewHolder(holder, position)
    }

    class BlogViewHolder(itemView: View) : ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val nickname: TextView = itemView.findViewById(R.id.nickname)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val content: TextView = itemView.findViewById(R.id.content)
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
        private val singleImage: ImageView = itemView.findViewById(R.id.single_image)
        private var gridImageAdapter3X3: GridImageAdapter3X3 = GridImageAdapter3X3()
        var imageDisplay: ImageDisplay? = null
        private lateinit var post: Post
        private val layoutManager: GridLayoutManager

        private fun showImage(pos: Int) {
            if (imageDisplay == null) return
            val views = mutableListOf<View>()
            if (singleImage.visibility == View.VISIBLE) views.add(singleImage)
            else {
                for (i in 0 until layoutManager.childCount)
                    if (gridImageAdapter3X3.getRealPosition(i) != -1) {
                        layoutManager.getChildAt(i)?.let { views.add(it) }
                    }
            }
            imageDisplay!!.showImages(post.images.map { it.url }.toTypedArray(), pos, views)
        }

        init {
            layoutManager = GridLayoutManager(recyclerView.context, 3)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = gridImageAdapter3X3
            val itemDecoration =
                GridSpacingItemDecoration(
                    3,
                    UIUtils.dp2px(recyclerView.context, 5f),
                    false
                )
            recyclerView.addItemDecoration(itemDecoration)
            singleImage.setOnClickListener { showImage(0) }
            gridImageAdapter3X3.onItemClickListener = object : OnItemClickListener {
                override fun onClick(view: View, pos: Int) {
                    val realPos = gridImageAdapter3X3.getRealPosition(pos)
                    if (realPos < 0) return
                    showImage(realPos)
                }
            }
        }

        override fun bind(data: Item) {
            val item = if (data.data is Post) data.data else return
            post = item
            item.iLikeAnimation = false
            item.iCommentAnimation = false
            ViewHolderHelper.update(nickname, item.user.nickname)
            ViewHolderHelper.update(icon, item.user.icon, false) { img, url ->
                GlideUtils.load(img, ImageItem(url), true)
                    .placeholder(R.drawable.shape_rect_image_placeholder)
                    .into(img)
            }
            ViewHolderHelper.update(time, DateUtils.format(item.postTime))
            ViewHolderHelper.update(content, item.content)
            content.visibility = if (item.content.isBlank()) View.GONE else View.VISIBLE
            singleImage.visibility = View.GONE
            recyclerView.visibility = View.GONE
            recyclerView.suppressLayout(false)
            when (item.images.size) {
                0 -> {
                }
                1 -> {
                    singleImage.visibility = View.VISIBLE
                    ViewHolderHelper.update(singleImage, item.images[0], false) { img, imgItem ->
                        GlideUtils.load(img, imgItem, true, sizeLimit = 600).into(img)
                    }
                }
                else -> {
                    recyclerView.visibility = View.VISIBLE
                    gridImageAdapter3X3.items = item.images
                    recyclerView.suppressLayout(true)
                }
            }
        }
    }

    override fun getContentItem(content: Post): Item {
        return Item(content.id, R.layout.item_post_details, content)
    }

}