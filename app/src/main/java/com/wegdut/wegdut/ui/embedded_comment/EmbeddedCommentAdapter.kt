package com.wegdut.wegdut.ui.embedded_comment

import android.annotation.SuppressLint
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.data.comment.Comment
import com.wegdut.wegdut.data.comment.ComplexComment
import com.wegdut.wegdut.scrollx.ScrollXAdapter
import com.wegdut.wegdut.ui.ImageDisplay
import com.wegdut.wegdut.ui.ViewHolderHelper
import com.wegdut.wegdut.utils.DateUtils
import com.wegdut.wegdut.utils.GlideUtils
import com.wegdut.wegdut.utils.NumberUtils
import com.wegdut.wegdut.view.ShineButton
import org.sufficientlysecure.htmltextview.HtmlTextView
import java.util.*

abstract class EmbeddedCommentAdapter<T> : ScrollXAdapter<ComplexComment>() {

    var imageDisplay: ImageDisplay? = null
    var commentListener: CommentListener? = null
    private val htmlTextViewCache = ViewCache(R.layout.item_comment_reply_textview)

    class ViewCache(private val layout: Int) {
        private val viewCache = Stack<View>()
        fun get(parent: ViewGroup): View {
            if (viewCache.isEmpty())
                return inflate(parent, layout)
            return viewCache.pop()
        }

        fun recycle(v: View) {
            viewCache.push(v)
        }
    }

    class ComplexCommentViewHolder(itemView: View, private val htmlTextViewCache: ViewCache) :
        ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val nickname: TextView = itemView.findViewById(R.id.nickname)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val likeBtn: ShineButton = itemView.findViewById(R.id.btn_like)
        private val content: TextView = itemView.findViewById(R.id.content)
        private val img: ImageView = itemView.findViewById(R.id.img)
        private val replyWrapper: LinearLayout = itemView.findViewById(R.id.reply_wrapper)
        var commentListener: CommentListener? = null
        var imageDisplay: ImageDisplay? = null

        override fun bind(data: Item) {
            val item = if (data.data is ComplexComment) data.data else return
            val comment = item.comment
            likeBtn.setOnClickListener { commentListener?.onLike(comment) }
            ViewHolderHelper.update(nickname, comment.user.nickname)
            ViewHolderHelper.update(time, DateUtils.format(comment.postTime))
            ViewHolderHelper.update(content, comment.content)
            content.visibility = if (comment.content.isBlank()) View.GONE else View.VISIBLE
            val likeText = NumberUtils.format(comment.like, " ")
            val likeState =
                ShineButton.ShineButtonState(comment.iLike, likeText, comment.iLikeAnimation)
            ViewHolderHelper.update(likeBtn, likeState) { _, _ ->
                likeBtn.setState(likeState)
            }
            ViewHolderHelper.update(icon, comment.user.icon, false) { _, _ ->
                GlideUtils.load(icon, ImageItem(comment.user.icon), true)
                    .placeholder(R.drawable.shape_rect_image_placeholder)
                    .into(icon)
            }
            if (comment.img != null) {
                img.visibility = View.VISIBLE
                img.setOnClickListener {
                    imageDisplay?.showImage(comment.img.url, it)
                }
                ViewHolderHelper.update(img, comment.img, false) { _, _ ->
                    GlideUtils.load(img, comment.img, true).into(img)
                }
            } else img.visibility = View.GONE
            parseReplies(item) // 因为含有时间，总是更新
        }

        private fun parseReplies(item: ComplexComment) {
            val nicknameColor =
                ResourcesCompat.getColor(replyWrapper.resources, R.color.colorPrimary, null)
            val timeColor = ResourcesCompat.getColor(replyWrapper.resources, R.color.grey_500, null)
            val commentColor =
                ResourcesCompat.getColor(replyWrapper.resources, R.color.comment_color, null)
            for (i in item.reply.withIndex()) {
                val c = i.value
                val nickname =
                    "<font color='$nicknameColor'>${Html.escapeHtml(c.user.nickname)}</font>"
                val re = c.replyUser
                val re2 = if (re == null || re.id == c.user.id) ""
                else "<font color='$timeColor'> 回复 ${re.nickname}</font>"
                val content = "<font color='$commentColor'>${Html.escapeHtml(c.content)}</font>"
                val time =
                    "<small><font color='$timeColor'>${Html.escapeHtml(DateUtils.format(c.postTime))}</font></small>"
                val html = "$nickname$re2: $content $time"
                val v = addHtml(html, i.index)
                v.setOnClickListener { commentListener?.onClickReply(item, c) }
            }
            var size = item.reply.size
            if (item.replyCount > item.reply.size) {
                val moreBtnColor =
                    ResourcesCompat.getColor(replyWrapper.resources, R.color.colorPrimary, null)
                val more =
                    "<font color='$moreBtnColor'>还有${item.replyCount - item.reply.size}条回复</font>"
                val v = addHtml(more, size)
                size++
                v.setOnClickListener { commentListener?.onClickShowMore(item) }
            }
            val count = replyWrapper.childCount - 1
            for (i in count downTo size) {
                val v = replyWrapper.getChildAt(i)
                replyWrapper.removeViewAt(i)
                htmlTextViewCache.recycle(v)
            }
            replyWrapper.visibility = if (size > 0) View.VISIBLE
            else View.GONE
        }

        private fun addHtml(html: String, index: Int): HtmlTextView {
            var v = replyWrapper.getChildAt(index)
            if (v == null) {
                v = htmlTextViewCache.get(replyWrapper)
                replyWrapper.addView(v)
            }
            val textView = v as HtmlTextView
            textView.setHtml(html)
            return textView
        }
    }

    class TabViewHolder(itemView: View) : ViewHolder(itemView) {
        private val tv: TextView = itemView.findViewById(R.id.text)

        @SuppressLint("SetTextI18n")
        override fun bind(data: Item) {
            val count = data.data as Int
            tv.text = "评论 $count"
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ComplexCommentViewHolder) {
            holder.commentListener = commentListener
            holder.imageDisplay = imageDisplay
        }
        super.onBindViewHolder(holder, position)
    }

    override fun getViewHolder(viewType: Int, view: View): ViewHolder? {
        return when (viewType) {
            R.layout.item_complex_comment -> ComplexCommentViewHolder(view, htmlTextViewCache)
            R.layout.item_embeded_comment_tab_view -> TabViewHolder(view)
            else -> null
        }
    }

    override fun getItem(data: ComplexComment): Item {
        return Item(data.comment.id, R.layout.item_complex_comment, data)
    }

    abstract fun getContentItem(content: T): Item

    interface CommentListener {
        fun onLike(comment: Comment)
        fun onClickReply(parent: ComplexComment, comment: Comment)
        fun onClickShowMore(complexComment: ComplexComment)
    }
}