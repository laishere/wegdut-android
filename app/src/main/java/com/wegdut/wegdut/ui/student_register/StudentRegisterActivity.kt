package com.wegdut.wegdut.ui.student_register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.login.LoginActivity
import com.wegdut.wegdut.utils.UIUtils

class StudentRegisterActivity : FragmentActivity() {

    companion object {
        const val STUDENT_NUMBER = "STUDENT_NUMBER"
        const val STUDENT_PASSWORD = "STUDENT_PASSWORD"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_register)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        startRegister()
    }

    private fun startRegister() {
        val fm = supportFragmentManager
        val result = fm.findFragmentById(R.id.result) ?: return
        val register = fm.findFragmentById(R.id.register) ?: return
        register.arguments = Bundle().apply {
            putString(STUDENT_NUMBER, intent.getStringExtra(STUDENT_NUMBER))
            putString(STUDENT_PASSWORD, intent.getStringExtra(STUDENT_PASSWORD))
        }
        val transaction = fm.beginTransaction()
        transaction.hide(result)
        transaction.commit()
    }

    fun startRegisterResult() {
        val fm = supportFragmentManager
        val register = fm.findFragmentById(R.id.register) ?: return
        val result = fm.findFragmentById(R.id.result) ?: return
        val transaction = fm.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in, R.anim.slide_out)
        transaction.show(result)
        transaction.hide(register)
        transaction.commit()
    }

    fun startLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        this.startActivity(intent)
    }
}