package com.wegdut.wegdut.ui.student_register

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.wegdut.wegdut.R
import com.wegdut.wegdut.ui.BaseDaggerFragment
import com.wegdut.wegdut.utils.MessageUtils
import javax.inject.Inject

class StudentRegisterFragment : BaseDaggerFragment(R.layout.fragment_student_register),
    StudentRegisterContract.View {
    private lateinit var userNameInput: TextInputEditText
    private lateinit var nicknameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var password2Input: TextInputEditText
    private lateinit var submitBtn: Button
    private var studentNumber: String? = null
    private var studentPassword: String? = null

    @Inject
    lateinit var presenter: StudentRegisterPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        updateStudentInfo()
        presenter.subscribe(this)
    }

    override fun onDestroyView() {
        presenter.unsubscribe()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.start()
    }

    override fun onDestroy() {
        presenter.stop()
        super.onDestroy()
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args == null) return
        studentNumber = args.getString(StudentRegisterActivity.STUDENT_NUMBER)
        studentPassword = args.getString(StudentRegisterActivity.STUDENT_PASSWORD)
        updateStudentInfo()
    }

    private fun updateStudentInfo() {
        if (this::userNameInput.isInitialized) {
            userNameInput.setText(studentNumber)
            nicknameInput.setText(studentNumber)
            passwordInput.setText(studentPassword)
            password2Input.setText(studentPassword)
        }
    }

    private fun initView(v: View) {
        userNameInput = v.findViewById(R.id.name)
        nicknameInput = v.findViewById(R.id.nickname)
        passwordInput = v.findViewById(R.id.password)
        password2Input = v.findViewById(R.id.password2)
        submitBtn = v.findViewById(R.id.btn_submit)

        submitBtn.setOnClickListener {
            submit()
        }

        val loginBtn: View = v.findViewById(R.id.btn_login)
        loginBtn.setOnClickListener { startLogin() }
    }

    private fun startLogin() {
        activity?.let {
            val act = it as StudentRegisterActivity
            act.startLogin()
        }
    }

    private fun submit() {
        presenter.register(
            userNameInput.text?.toString() ?: "",
            nicknameInput.text?.toString() ?: "",
            passwordInput.text?.toString() ?: "",
            password2Input.text?.toString() ?: "",
            studentNumber!!,
            studentPassword!!
        )
    }

    override fun setError(error: String) {
        MessageUtils.info(context, error)
    }

    override fun setSubmitEnabled(enabled: Boolean) {
        submitBtn.isEnabled = enabled
    }

    override fun onSuccess() {
        activity?.let {
            val act = it as StudentRegisterActivity
            act.startRegisterResult()
        }
    }

    override fun setUsernameError(error: String) {
        userNameInput.error = error
    }

    override fun setPasswordError(error: String) {
        passwordInput.error = error
    }

    override fun setPassword2Error(error: String) {
        password2Input.error = error
    }

    override fun setNicknameError(error: String) {
        nicknameInput.error = error
    }
}