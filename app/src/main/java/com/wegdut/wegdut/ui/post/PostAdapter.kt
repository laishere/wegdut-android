package com.wegdut.wegdut.ui.post

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wegdut.wegdut.MyApplication
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.scrollx.ScrollXAdapter
import com.wegdut.wegdut.ui.GridSpacingItemDecoration
import com.wegdut.wegdut.ui.ImageDisplay
import com.wegdut.wegdut.ui.ViewHolderHelper
import com.wegdut.wegdut.utils.DateUtils
import com.wegdut.wegdut.utils.GlideUtils
import com.wegdut.wegdut.utils.NumberUtils
import com.wegdut.wegdut.utils.UIUtils
import com.wegdut.wegdut.view.ShineButton
import javax.inject.Inject

class PostAdapter @Inject constructor() : ScrollXAdapter<Post>() {
    var postOnClickListener: PostOnClickListener? = null
    var imageDisplay: ImageDisplay? = null

    class PostViewHolder(itemView: View) : ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val nickname: TextView = itemView.findViewById(R.id.nickname)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val content: TextView = itemView.findViewById(R.id.content)
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
        private val singleImage: ImageView = itemView.findViewById(R.id.single_image)
        private var gridImageAdapter3X3: GridImageAdapter3X3 = GridImageAdapter3X3()
        private val replyBtn: ShineButton = itemView.findViewById(R.id.btn_reply)
        private val likeBtn: ShineButton = itemView.findViewById(R.id.btn_like)
        private val deleteBtn: View = itemView.findViewById(R.id.delete)
        private lateinit var post: Post
        private val layoutManager: GridLayoutManager
        var postOnClickListener: PostOnClickListener? = null
        var imageDisplay: ImageDisplay? = null

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
            likeBtn.setOnClickListener {
                postOnClickListener?.onLike(adapterPosition)
            }
            deleteBtn.setOnClickListener {
                postOnClickListener?.onDelete(adapterPosition)
            }
        }

        override fun bind(data: Item) {
            post = data.data as Post
            val updateButton = { btn: ShineButton, state: ShineButton.ShineButtonState ->
                btn.setState(state)
            }
            val likeBtnState = ShineButton.ShineButtonState(
                post.iLike, NumberUtils.format(post.like, " "), post.iLikeAnimation
            )
            val replyBtnState = ShineButton.ShineButtonState(
                post.iComment,
                NumberUtils.format(post.comment, " "),
                post.iCommentAnimation
            )
            ViewHolderHelper.update(likeBtn, likeBtnState, update = updateButton)
            ViewHolderHelper.update(replyBtn, replyBtnState, update = updateButton)
            post.iLikeAnimation = false
            post.iCommentAnimation = false
            ViewHolderHelper.update(nickname, post.user.nickname)
            ViewHolderHelper.update(icon, post.user.icon) { img, url ->
                GlideUtils.loadIcon(img, url, true)
            }
            ViewHolderHelper.update(time, DateUtils.format(post.postTime))
            ViewHolderHelper.update(content, post.content)
            content.visibility = if (post.content.isBlank()) View.GONE else View.VISIBLE
            singleImage.visibility = View.GONE
            recyclerView.visibility = View.GONE
            recyclerView.suppressLayout(false)
            deleteBtn.visibility =
                if (MyApplication.user?.id == post.user.id) View.VISIBLE
                else View.GONE
            when (post.images.size) {
                0 -> {
                }
                1 -> {
                    singleImage.visibility = View.VISIBLE
                    ViewHolderHelper.update(singleImage, post.images[0], false) { img, item ->
                        GlideUtils.load(img, item, true, sizeLimit = 600).into(img)
                    }
                }
                else -> {
                    recyclerView.visibility = View.VISIBLE
                    gridImageAdapter3X3.items = post.images
                    recyclerView.suppressLayout(true)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            holder.imageDisplay = imageDisplay
            holder.postOnClickListener = postOnClickListener
        }
        super.onBindViewHolder(holder, position)
    }

    interface PostOnClickListener {
        fun onLike(pos: Int)
        fun onDelete(pos: Int)
    }

    override fun getViewHolder(viewType: Int, view: View): ViewHolder? {
        if (viewType == R.layout.item_post)
            return PostViewHolder(view)
        return null
    }

    override fun getItem(data: Post): Item {
        return Item(data.id, R.layout.item_post, data)
    }
}