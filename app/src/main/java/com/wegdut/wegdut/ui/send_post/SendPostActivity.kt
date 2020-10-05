package com.wegdut.wegdut.ui.send_post

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.config.Config
import com.wegdut.wegdut.dialog.LoadingDialog
import com.wegdut.wegdut.ui.BaseTypeRVAdapter
import com.wegdut.wegdut.ui.GridSpacingItemDecoration
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.utils.MessageUtils
import com.wegdut.wegdut.utils.PermissionUtils
import com.wegdut.wegdut.utils.UIUtils
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject


class SendPostActivity : DaggerAppCompatActivity(), SendPostContract.View {

    @Inject
    lateinit var presenter: SendPostContract.Presenter

    private val adapter = ImageAdapter()
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageHint: View
    private val selectedImages = mutableListOf<Uri>()
    private lateinit var postMenu: MenuItem
    private lateinit var content: TextView
    private val loadingDialog = LoadingDialog(this)

    companion object {
        const val REQUEST_CODE_CHOOSE = 1
        const val MAX_IMAGE_COUNT = 9
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_post)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        content = findViewById(R.id.content)
        recyclerView = findViewById(R.id.recycler_view)
        imageHint = findViewById(R.id.image_hint)
        adapter.items = listOf(BaseTypeRVAdapter.Item(0, R.layout.item_add_image, null))
        adapter.onItemClickListener = object : ListRVAdapter.OnItemClickListener {
            override fun onClick(view: View, pos: Int) {
                refreshImages()
                if (adapter.getItemViewType(pos) == R.layout.item_add_image) {
                    PermissionUtils.pickImage(this@SendPostActivity, "需要存储权限哦") {
                        pickImage(MAX_IMAGE_COUNT - selectedImages.size)
                    }
                }
            }
        }
        recyclerView.adapter = adapter
        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                3,
                resources.getDimensionPixelSize(R.dimen.medium_text_margin), false
            )
        )
        val simpleImageHelperCallback =
            object : ImageHelperCallback<BaseTypeRVAdapter.Item>(adapter) {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val i = viewHolder.adapterPosition
                    if (adapter.getItemViewType(i) == R.layout.item_add_image)
                        return 0
                    return super.getMovementFlags(recyclerView, viewHolder)
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    super.onSwiped(viewHolder, direction)
                    onImageCountChanged()
                    val cnt = adapter.itemCount
                    if (cnt < MAX_IMAGE_COUNT && adapter.getItemViewType(cnt - 1) != R.layout.item_add_image) {
                        val items = adapter.items?.toMutableList() ?: mutableListOf()
                        items.add(BaseTypeRVAdapter.Item(0, R.layout.item_add_image, null))
                        adapter.setItemsQuietly(items)
                        adapter.notifyItemInserted(cnt)
                    }
                }

                override fun canDropOver(
                    recyclerView: RecyclerView,
                    current: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val i = target.adapterPosition
                    if (adapter.getItemViewType(i) == R.layout.item_add_image)
                        return false
                    return true
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleImageHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        postMenu = toolbar.menu.findItem(R.id.post)
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.post) {
                postBlog()
            }
            true
        }
        presenter.start()
        presenter.subscribe(this)
    }

    override fun onDestroy() {
        loadingDialog.dismiss()
        presenter.unsubscribe()
        presenter.stop()
        super.onDestroy()
    }

    private fun postBlog() {
        val text = content.text.toString()
        refreshImages()
        if (text.isBlank() && selectedImages.isEmpty()) {
            MessageUtils.info(this, "请输入内容或选择图片")
            return
        }
        presenter.sendPost(text, selectedImages)
    }

    private fun pickImage(cnt: Int) {
        Matisse.from(this)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(cnt)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .imageEngine(GlideEngine())
            .capture(true)
            .captureStrategy(CaptureStrategy(false, Config.fileProviderAuthorities))
            .forResult(REQUEST_CODE_CHOOSE)
    }

    private fun setImages() {
        val arr = selectedImages.map {
            BaseTypeRVAdapter.Item(0, R.layout.item_square_image, it)
        }.toMutableList()
        if (arr.size < MAX_IMAGE_COUNT) arr.add(
            BaseTypeRVAdapter.Item(
                0,
                R.layout.item_add_image,
                null
            )
        )
        adapter.items = arr
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CODE_CHOOSE || resultCode != Activity.RESULT_OK) return
        selectedImages.addAll(Matisse.obtainResult(data))
        setImages()
        onImageCountChanged()
    }

    private fun refreshImages() {
        selectedImages.clear()
        adapter.items?.forEach {
            if (it.data is Uri)
                selectedImages.add(it.data)
        }
    }

    private fun onImageCountChanged() {
        val isShow = imageHint.visibility == View.VISIBLE
        val toShow = adapter.itemCount > 1
        if (isShow == toShow) return
        UIUtils.fade(imageHint, toShow)
    }

    override fun showLoadingDialog() {
        loadingDialog.show()
    }

    override fun dismissLoadingDialog(success: Boolean, message: String) {
        if (success) loadingDialog.successAndDismiss(message) {
            supportFinishAfterTransition()
        }
        else loadingDialog.errorAndDismiss(message)
    }

    override fun setAction(action: String) {
        loadingDialog.action = action
    }

    override fun setSendEnabled(enabled: Boolean) {
        postMenu.isEnabled = enabled
    }
}