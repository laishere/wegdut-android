package com.wegdut.wegdut.ui.setting

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.user.User
import com.wegdut.wegdut.ui.BaseTypeRVAdapter
import com.wegdut.wegdut.utils.GlideUtils

class SettingAdapter : BaseTypeRVAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflate(parent, viewType)
        return when (viewType) {
            R.layout.item_setting_user -> UserViewHolder(view)
            R.layout.item_setting_item -> NormalViewHolder(view)
            else -> StaticViewHolder(view)
        }
    }

    class UserViewHolder(itemView: View) : ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val nickname: TextView = itemView.findViewById(R.id.nickname)
        override fun bind(data: Item) {
            val item = data.data as SettingItem
            val user = item.data as User? ?: return
            GlideUtils.loadIcon(icon, user.icon, true)
            nickname.text = user.nickname
        }
    }

    class NormalViewHolder(itemView: View) : ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        override fun bind(data: Item) {
            val item = data.data as SettingItem
            val name2 = item.data as String
            name.text = name2
        }
    }
}