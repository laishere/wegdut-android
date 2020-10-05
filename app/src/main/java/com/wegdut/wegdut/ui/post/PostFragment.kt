package com.wegdut.wegdut.ui.post


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.ui.BaseDaggerFragment
import com.wegdut.wegdut.ui.ImageDisplay
import com.wegdut.wegdut.ui.ListRVAdapter
import com.wegdut.wegdut.ui.Reselectable
import com.wegdut.wegdut.ui.post_details.PostDetailsActivity
import com.wegdut.wegdut.ui.send_post.SendPostActivity
import com.wegdut.wegdut.utils.MessageUtils
import com.wegdut.wegdut.utils.UIUtils
import javax.inject.Inject

class PostFragment : BaseDaggerFragment(R.layout.fragment_post), Reselectable {
    @Inject
    lateinit var scrollXView: PostContract.View

    @Inject
    lateinit var presenter: PostContract.Presenter
    private lateinit var adapter: PostAdapter
    private var toDeleteId = 0L
    private lateinit var recyclerView: RecyclerView
    private var hasReturnTransition = false
    private lateinit var appBarLayout: AppBarLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UIUtils.addStatusBarPadding(view)
        recyclerView = view.findViewById(R.id.recycler_view)
        appBarLayout = view.findViewById(R.id.app_bar_layout)
        UIUtils.applyScrollElevation(appBarLayout, recyclerView)
        adapter = scrollXView.adapter as PostAdapter
        adapter.imageDisplay = object : ImageDisplay(activity!!) {
            override fun onImageShow() {
                hasReturnTransition = true
            }

            override fun onSharedElementEnd() {
                hasReturnTransition = false
                scrollXView.updateVisibleItems()
            }
        }
        adapter.postOnClickListener = blogOnClickListener
        scrollXView.adapter.onItemClickListener = object : ListRVAdapter.OnItemClickListener {
            override fun onClick(view: View, pos: Int) {
                val blog = adapter.items?.get(pos) ?: return
                if (blog.data is Post)
                    startExploreDetails(blog.id)
            }
        }

        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.post -> startPostBlog()
                else -> return@setOnMenuItemClickListener false
            }
            return@setOnMenuItemClickListener true
        }
        scrollXView.bind(view)
    }

    override fun onStart() {
        super.onStart()
        if (!hasReturnTransition) // 避免在共享元素动画运行期间更新列表元素
            scrollXView.updateVisibleItems()
    }

    override fun onDestroyView() {
        scrollXView.unbind()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scrollXView.start()
    }

    override fun onDestroy() {
        scrollXView.stop()
        super.onDestroy()
    }

    private val blogOnClickListener = object : PostAdapter.PostOnClickListener {
        override fun onLike(pos: Int) {
            val adapter = scrollXView.adapter
            val post = adapter.items!![pos].data as Post
            val like = !post.iLike
            presenter.toggleLike(post, {}) {
                val err = MyException.handle(it)
                val action = if (like) "点赞" else "取消点赞"
                MessageUtils.info(context, "${action}失败：$err")
            }
        }

        override fun onDelete(pos: Int) {
            val item = adapter.items?.getOrNull(pos) ?: return
            if (item.data !is Post) return
            toDeleteId = item.data.id
            showDeleteDialog()
        }
    }

    private fun startPostBlog() {
        presenter.canSendPost({
            val intent = Intent(activity, SendPostActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            activity?.startActivity(intent)
        }) {
            val err = MyException.handle(it)
            MessageUtils.info(context, "无法发表动态\n$err")
        }
    }

    private fun startExploreDetails(id: Long) {
        val intent = Intent(activity, PostDetailsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(PostDetailsActivity.POST_ID, id)
        activity?.startActivity(intent)
    }

    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(context!!, R.style.DangerAlertDialog)
            .setMessage("是否删除？")
            .setPositiveButton("删除") { _, _ ->
                doDelete()
            }
            .setNegativeButton("取消") { _, _ ->
            }
            .show()
    }

    private fun doDelete() {
        val post = adapter.items?.find {
            it.data is Post && it.data.id == toDeleteId
        } ?: return
        presenter.delete(post.data as Post, {}) {
            val err = MyException.handle(it)
            MessageUtils.info(context, "删除失败：$err")
        }
    }

    override fun reselect() {
        recyclerView.scrollToPosition(0)
        appBarLayout.setExpanded(true)
        appBarLayout.isHovered = false
    }
}