package com.wegdut.wegdut.ui.message.reply

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.wegdut.wegdut.MyApplication
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.ImageItem
import com.wegdut.wegdut.data.message.reply.ReplyMessage
import com.wegdut.wegdut.scroll.ScrollAdapter
import com.wegdut.wegdut.ui.ViewHolderHelper
import com.wegdut.wegdut.utils.DateUtils
import com.wegdut.wegdut.utils.GlideUtils
import com.wegdut.wegdut.utils.Utils.toText
import com.wegdut.wegdut.view.ShineButton
import org.sufficientlysecure.htmltextview.HtmlTextView
import javax.inject.Inject

class ReplyAdapter @Inject constructor() : ScrollAdapter<ReplyMessage>() {

    var replyViewHolderCallback: ReplyViewHolderCallback? = null

    class ReplyViewHolder(itemView: View) : ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val nickname: TextView = itemView.findViewById(R.id.nickname)
        private val postTime: TextView = itemView.findViewById(R.id.time)
        private val content: TextView = itemView.findViewById(R.id.content)
        private val img: ImageView = itemView.findViewById(R.id.img)
        private val replyType: TextView = itemView.findViewById(R.id.type)
        private val summary: HtmlTextView = itemView.findViewById(R.id.summary)
        private val badge: View = itemView.findViewById(R.id.badge)
        private val replyBtn: View = itemView.findViewById(R.id.btn_reply)
        private val likeBtn: ShineButton = itemView.findViewById(R.id.btn_like)
        var replyViewHolderCallback: ReplyViewHolderCallback? = null

        @SuppressLint("SetTextI18n")
        override fun bind(data: Item) {
            val item = data.data as ReplyMessage
            val comment = item.comment
            replyBtn.setOnClickListener {
                replyViewHolderCallback?.reply(item)
            }
            likeBtn.setOnClickListener {
                replyViewHolderCallback?.like(item)
            }
            val likeState = ShineButton.ShineButtonState(comment.iLike, "", comment.iLikeAnimation)
            ViewHolderHelper.update(likeBtn, likeState) { _, _ ->
                likeBtn.setState(likeState)
            }
            comment.iLikeAnimation = false
            ViewHolderHelper.update(nickname, comment.user.nickname)
            ViewHolderHelper.update(postTime, DateUtils.format(comment.postTime))
            ViewHolderHelper.update(content, comment.content)
            badge.visibility = if (item.hasRead) View.GONE else View.VISIBLE
            val type = "回复了你的" + comment.replyType.toText()
            ViewHolderHelper.update(replyType, type) { _, _ ->
                replyType.text = type
            }
            val nicknameColor =
                ResourcesCompat.getColor(summary.resources, R.color.colorPrimary, null)
            val summaryHtml =
                "<font color='$nicknameColor'>${MyApplication.user?.nickname ?: "[未登录]"}: </font>${item.originAbstract}"
            ViewHolderHelper.update(summary, summaryHtml) { _, _ ->
                summary.setHtml(summaryHtml)
            }
            ViewHolderHelper.update(icon, comment.user.icon, false) { _, _ ->
                GlideUtils.load(icon, ImageItem(comment.user.icon), true)
                    .placeholder(R.drawable.shape_rect_image_placeholder)
                    .centerCrop()
                    .into(icon)
            }
            if (comment.img == null) img.visibility = View.GONE
            else {
                img.visibility = View.VISIBLE
                ViewHolderHelper.update(img, comment.img, false) { _, _ ->
                    GlideUtils.load(img, comment.img, true).into(img)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ReplyViewHolder)
            holder.replyViewHolderCallback = replyViewHolderCallback
        super.onBindViewHolder(holder, position)
    }

    override fun getViewHolder(viewType: Int, view: View): ViewHolder? {
        if (viewType == R.layout.item_message_reply)
            return ReplyViewHolder(view)
        return null
    }

    override fun getItem(data: ReplyMessage): Item {
        return Item(data.id, R.layout.item_message_reply, data)
    }

    interface ReplyViewHolderCallback {
        fun reply(item: ReplyMessage)
        fun like(item: ReplyMessage)
    }

}