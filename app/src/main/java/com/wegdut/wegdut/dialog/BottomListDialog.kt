package com.wegdut.wegdut.dialog

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.wegdut.wegdut.R

abstract class BottomListDialog(val context: Context) {
    protected lateinit var dialog: BottomSheetDialog
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var titleTextView: TextView

    open fun show() {
        initDialog()
        dialog.show()
    }

    private fun initDialog() {
        if (this::dialog.isInitialized) return
        dialog = BottomSheetDialog(context, R.style.BottomListDialog)
        dialog.setContentView(R.layout.dialog_bottom_list)
        recyclerView = dialog.findViewById(R.id.recycler_view)!!
        titleTextView = dialog.findViewById(R.id.title)!!
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = getAdapter()
        val title = getTitle()
        if (title == null)
            titleTextView.visibility = View.GONE
        else {
            titleTextView.text = title
            titleTextView.visibility = View.VISIBLE
        }
        onInit()
    }

    abstract fun getAdapter(): RecyclerView.Adapter<*>
    abstract fun getTitle(): CharSequence?

    abstract fun onInit()

    fun dismiss() {
        dialog.dismiss()
    }
}