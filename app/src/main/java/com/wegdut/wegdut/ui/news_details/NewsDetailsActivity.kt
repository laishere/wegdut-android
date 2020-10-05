package com.wegdut.wegdut.ui.news_details

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import com.wegdut.wegdut.R
import com.wegdut.wegdut.config.Config
import com.wegdut.wegdut.utils.UIUtils
import okhttp3.HttpUrl.Companion.toHttpUrl


class NewsDetailsActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    companion object {
        const val ID = "ID"
        const val TITLE = "TITLE"
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)
        webView = findViewById(R.id.web_view)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
        UIUtils.setSwipeRefreshColor(swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            webView.reload()
        }
        val appBarLayout: AppBarLayout = findViewById(R.id.appBarLayout)
        appBarLayout.isHovered = true
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        webView.setDownloadListener(downloadListener)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = webViewClient
        webView.webChromeClient = webChromeClient
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        val id = intent.getLongExtra(ID, 0)
        val url = Config.apiBaseUrl.toHttpUrl().resolve("/news?id=$id")
        webView.loadUrl(url.toString())
        val titleTextView: TextView = findViewById(R.id.title)
        titleTextView.text = intent.getStringExtra(TITLE)
    }

    private val webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            swipeRefreshLayout.isRefreshing = newProgress < 100
        }

    }

    private val webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (handleUrl(view, request.url.toString()))
                    return true
            }
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (handleUrl(view, url))
                return true
            return super.shouldOverrideUrlLoading(view, url)
        }
    }

    private fun handleUrl(view: WebView, url: String): Boolean {
        if (url.startsWith("http")) {
            view.loadUrl(url)
            return true
        }
        if (url.startsWith("mailto:")) {
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(url)))
            return true
        }
        if (url.startsWith("tel:")) {
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(url)))
            return true
        }
        return false
    }

    private val downloadListener =
        DownloadListener { url, _, _, _, _ ->
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
}