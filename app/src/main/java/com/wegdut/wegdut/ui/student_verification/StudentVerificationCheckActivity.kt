package com.wegdut.wegdut.ui.student_verification

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.wegdut.wegdut.MyException
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.R
import com.wegdut.wegdut.data.student_verification.StudentVerificationRepository
import com.wegdut.wegdut.ui.Jobs
import com.wegdut.wegdut.utils.UIUtils
import com.wegdut.wegdut.view.LoadingWrapper
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.*
import javax.inject.Inject

class StudentVerificationCheckActivity : DaggerAppCompatActivity() {

    private lateinit var imgView: ImageView
    private lateinit var textView: TextView
    private lateinit var btn: Button
    private lateinit var wrapper: View
    private lateinit var loadingWrapper: LoadingWrapper

    @Inject
    lateinit var repository: StudentVerificationRepository
    private val jobs = Jobs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_verification_check)
        initView()
        check()
    }

    override fun onDestroy() {
        jobs.clear()
        super.onDestroy()
    }

    private fun initView() {
        loadingWrapper = findViewById(R.id.loading_wrapper)
        imgView = findViewById(R.id.img)
        textView = findViewById(R.id.text)
        btn = findViewById(R.id.btn)
        wrapper = findViewById(R.id.wrapper)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
    }

    private fun check() {
        val job = CoroutineScope(Dispatchers.Main).launch {
            loadingWrapper.loading = true
            try {
                val verified = withContext(Dispatchers.IO) {
                    repository.isVerified()
                }
                onResult(verified)
                loadingWrapper.error = null
            } catch (e: CancellationException) {
            } catch (e: Throwable) {
                MyLog.error(this, e)
                val err = MyException.handle(e)
                loadingWrapper.error = err
            }
            loadingWrapper.loading = false
        }
        jobs.add(job)
    }

    private fun onResult(verified: Boolean) {
        UIUtils.fade(wrapper, true)
        if (verified) {
            imgView.setImageResource(R.drawable.ic_check_circle)
            textView.text = "已完成学生认证"
            btn.text = "修改学生认证"
        } else {
            imgView.setImageResource(R.drawable.ic_list)
            textView.text = "未进行学生认证"
            btn.text = "进行学生认证"
        }
        imgView.isActivated = verified
        textView.isActivated = verified
        btn.setOnClickListener {
            startVerification()
        }
    }

    private fun startVerification() {
        val intent = Intent(this, StudentVerificationActivity::class.java)
        startActivity(intent)
        finish()
    }
}