package com.wegdut.wegdut.ui.student_register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.wegdut.wegdut.R

class StudentRegisterResultFragment : Fragment(R.layout.fragment_student_register_result) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginBtn: View = view.findViewById(R.id.btn_login)
        loginBtn.setOnClickListener { startLogin() }
    }

    private fun startLogin() {
        activity?.let {
            val act = it as StudentRegisterActivity
            act.startLogin()
        }
    }
}