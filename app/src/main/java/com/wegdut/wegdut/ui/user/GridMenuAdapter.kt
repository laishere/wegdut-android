package com.wegdut.wegdut.ui.user

import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.ListRVAdapter

class GridMenuAdapter(items: List<MenuItem>? = null) :
    ListRVAdapter<MenuItem, GridMenuAdapter.MenuViewHolder>(items) {

    class MenuViewHolder(itemView: View) : ListRVAdapter.ViewHolder<MenuItem>(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val title: TextView = itemView.findViewById(R.id.title)
        override fun bind(data: MenuItem) {
            icon.setImageDrawable(data.icon)
            title.text = data.title
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_grid_menu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = inflate(parent, viewType)
        return MenuViewHolder(view)
    }
}