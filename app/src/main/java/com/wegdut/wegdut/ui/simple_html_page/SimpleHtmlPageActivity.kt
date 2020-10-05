package com.wegdut.wegdut.ui.simple_html_page

import android.os.Bundle
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.wegdut.wegdut.R
import com.wegdut.wegdut.utils.UIUtils

class SimpleHtmlPageActivity : AppCompatActivity() {

    companion object {
        const val TITLE = "title"
        const val ASSET_HTML = "html"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_html)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        val title: TextView = findViewById(R.id.title)
        title.text = intent.getStringExtra(TITLE)
        val webView: WebView = findViewById(R.id.web_view)
        webView.loadUrl("file:///android_asset/${intent.getStringExtra(ASSET_HTML)}")
    }
}