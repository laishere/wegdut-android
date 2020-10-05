package com.wegdut.wegdut.ui.post_details

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.ui.ImageDisplay
import com.wegdut.wegdut.utils.UIUtils
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class PostDetailsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var postDetailsView: PostDetailsView
    private var hasReturnTransition: Boolean = false

    companion object {
        const val POST_ID = "POST_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        UIUtils.applyScrollElevation(
            findViewById(R.id.app_bar_layout),
            findViewById<RecyclerView>(R.id.recycler_view)
        )
        val id = intent.getLongExtra(POST_ID, 0)
        val imageDisplay = object : ImageDisplay(this) {
            override fun onImageShow() {
                hasReturnTransition = true
            }

            override fun onSharedElementEnd() {
                postDetailsView.updateVisibleItems()
                hasReturnTransition = false
            }
        }
        val view: View = findViewById(android.R.id.content)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        postDetailsView.start()
        postDetailsView.bind(view, this, id, ContentType.POST, imageDisplay)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        postDetailsView.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        if (!hasReturnTransition)
            postDetailsView.updateVisibleItems()
    }

    override fun onDestroy() {
        postDetailsView.unbind()
        postDetailsView.stop()
        super.onDestroy()
    }
}