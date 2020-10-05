package com.wegdut.wegdut.ui.email_register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.login.LoginActivity
import com.wegdut.wegdut.utils.UIUtils

class EmailRegisterActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_register)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        UIUtils.displayBackInToolbar(this, toolbar)
        startRegister()
    }

    private fun startRegister() {
        val fm = supportFragmentManager
        val result = fm.findFragmentById(R.id.result) ?: return
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