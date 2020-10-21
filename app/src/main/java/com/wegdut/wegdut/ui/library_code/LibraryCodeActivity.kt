package com.wegdut.wegdut.ui.library_code

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.user.StudentDto
import com.wegdut.wegdut.utils.QRCodeUtils
import com.wegdut.wegdut.utils.UIUtils
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LibraryCodeActivity : DaggerAppCompatActivity(), LibraryCodeContract.View {
    private lateinit var imageView: ImageView
    private lateinit var errorTextView: TextView
    private lateinit var progressBar: ContentLoadingProgressBar
    private var bitmap: Bitmap? = null

    @Inject
    lateinit var presenter: LibraryCodeContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_PhotoViewActivity)
        setContentView(R.layout.activity_library_code)
        val content: View = findViewById(android.R.id.content)
        UIUtils.imageFullScreen(content, true)
        val attr = window.attributes
        attr.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        window.attributes = attr
        content.setOnClickListener {
            supportFinishAfterTransition()
        }
        imageView = findViewById(R.id.image)
        errorTextView = findViewById(R.id.error)
        progressBar = findViewById(R.id.progress_bar)
        presenter.start()
        presenter.subscribe(this)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        presenter.stop()
        super.onDestroy()
        bitmap?.recycle()
    }

    private fun setNumber(number: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val b = withContext(Dispatchers.Default) {
                QRCodeUtils.generate(number, 512, 512)
            }
            imageView.setImageBitmap(b)
            bitmap?.recycle()
            bitmap = b
        }
    }

    override fun setStudent(dto: StudentDto) {
        setNumber(dto.number)
    }

    override fun setError(error: String?) {
        if (error == null)
            errorTextView.visibility = View.INVISIBLE
        else {
            errorTextView.text = error
            errorTextView.visibility = View.VISIBLE
        }
    }

    override fun setLoading(loading: Boolean) {
        if (loading) {
            progressBar.show()
            imageView.visibility = View.INVISIBLE
        } else {
            progressBar.hide()
            imageView.visibility = View.VISIBLE
        }
    }
}