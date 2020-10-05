package com.wegdut.wegdut.ui.message.like

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.wegdut.wegdut.MyApplication
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.message.like.LikeMessage
import com.wegdut.wegdut.glide.GlideApp
import com.wegdut.wegdut.scroll.ScrollAdapter
import com.wegdut.wegdut.ui.ViewHolderHelper
import com.wegdut.wegdut.utils.DateUtils
import com.wegdut.wegdut.utils.Utils.toText
import org.sufficientlysecure.htmltextview.HtmlTextView
import javax.inject.Inject

class LikeAdapter @Inject constructor() : ScrollAdapter<LikeMessage>() {

    class LikeViewHolder(itemView: View) : ViewHolder(itemView) {
        private val icons = mutableListOf<ImageView>()
        private val iconWrapper: ViewGroup = itemView.findViewById(R.id.icon_wrapper)
        private val title: HtmlTextView = itemView.findViewById(R.id.title)
        private val summary: HtmlTextView = itemView.findViewById(R.id.summary)
        private val time: TextView = itemView.findViewById(R.id.time)
        private val badge: View = itemView.findViewById(R.id.badge)

        @SuppressLint("SetTextI18n")
        override fun bind(data: Item) {
            val item = data.data as LikeMessage
            val users = item.latestUsers
            val userNickname = users[0].nickname
            val captionColor = ResourcesCompat.getColor(title.resources, R.color.text_caption, null)
            val num = if (users.size == 1) "" else " 等${users.size}人"
            val likeText = "赞了你的" + item.type.toText()
            val titleHtml = "$userNickname$num <font color='$captionColor'>$likeText</font>"
            badge.visibility = if (item.hasRead) View.GONE else View.VISIBLE
            ViewHolderHelper.update(title, titleHtml) { _, _ ->
                title.setHtml(titleHtml)
            }
            val nicknameColor =
                ResourcesCompat.getColor(summary.resources, R.color.colorPrimary, null)
            val summaryHtml =
                "<font color='$nicknameColor'>${MyApplication.user?.nickname ?: "[未登录]"}: </font>${item.originAbstract}"
            ViewHolderHelper.update(summary, summaryHtml) { _, _ ->
                summary.setHtml(summaryHtml)
            }
            ViewHolderHelper.update(time, DateUtils.format(item.createdAt))
            ViewHolderHelper.update(iconWrapper, users) { _, _ ->
                iconWrapper.removeAllViews()
                val images = users.map { it.icon }
                if (images.size > icons.size) {
                    val d = images.size - icons.size
                    for (i in 1..d) {
                        val img = LayoutInflater.from(iconWrapper.context)
                            .inflate(R.layout.item_message_like_user_icon, iconWrapper, false)
                        icons.add(img as ImageView)
                    }
                }
                for (i in images.withIndex()) {
                    val icon = icons[i.index]
                    GlideApp.with(icon)
                        .load(i.value)
                        .placeholder(R.drawable.shape_rect_image_placeholder)
                        .centerCrop()
                        .into(icon)
                    iconWrapper.addView(icon)
                }
            }
        }
    }

    override fun getViewHolder(viewType: Int, view: View): ViewHolder? {
        if (viewType == R.layout.item_message_like)
            return LikeViewHolder(view)
        return null
    }

    override fun getItem(data: LikeMessage): Item {
        return Item(data.id, R.layout.item_message_like, data)
    }

}