package com.wegdut.wegdut.email_register

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.wegdut.wegdut.ApiTestUtils
import com.wegdut.wegdut.MyLog
import com.wegdut.wegdut.PresenterTestUtils.runBlocking
import com.wegdut.wegdut.api.RegisterApi
import com.wegdut.wegdut.ui.email_register.EmailRegisterContract
import com.wegdut.wegdut.ui.email_register.EmailRegisterPresenter
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class EmailRegisterPresenterTest {

    @Mock
    lateinit var registerApi: RegisterApi

    @Mock
    lateinit var view: EmailRegisterContract.View

    private val presenter = EmailRegisterPresenter()
    private lateinit var order: InOrder

    @Before
    fun setup() {
        MyLog.isTesting = true
        MockitoAnnotations.initMocks(this)
        order = inOrder(view, registerApi)
        presenter.api = registerApi
        presenter.start()
        presenter.subscribe(view)
    }

    @Test
    fun `Email注册测试`() {
        val username = "123456"
        val nickname = "1"
        val password = "123456"
        val email = "username@abc.com"

        // 用户名长度小于3
        presenter.runBlocking {
            presenter.register("12", nickname, password, password, email)
        }
        order.verify(view).setUsernameError(any())
        order.verifyNoMoreInteractions()

        // 昵称为空
        presenter.runBlocking {
            presenter.register(username, "", password, password, email)
        }
        order.verify(view).setNicknameError(any())
        order.verifyNoMoreInteractions()

        // 密码长度小于6
        presenter.runBlocking {
            presenter.register(username, nickname, "12345", password, email)
        }
        order.verify(view).setPasswordError(any())
        order.verifyNoMoreInteractions()

        // 密码不一致
        presenter.runBlocking {
            presenter.register(username, nickname, password, password + "a", email)
        }
        order.verify(view).setPassword2Error(any())
        order.verifyNoMoreInteractions()

        // 邮箱格式不正确
        presenter.runBlocking {
            presenter.register(username, nickname, password, password, "a@abc.a")
        }
        order.verify(view).setEmailError(any())
        order.verifyNoMoreInteractions()

        // api返回正常结果时
        var call = ApiTestUtils.okWrapper(1L as Long?)
        `when`(registerApi.register(any())).thenReturn(call)
        presenter.runBlocking {
            presenter.register(username, nickname, password, password, email)
        }
        order.verify(view).setSubmitEnabled(false)
        order.verify(registerApi).register(any())
        order.verify(view).onRegisterSuccess()
        order.verify(view).setSubmitEnabled(true)
        order.verifyNoMoreInteractions()

        // api返回异常结果时
        val error = "用户名已存在"
        call = ApiTestUtils.errorWrapper(error)
        `when`(registerApi.register(any())).thenReturn(call)
        presenter.runBlocking {
            presenter.register(username, nickname, password, password, email)
        }
        order.verify(view).setSubmitEnabled(false)
        order.verify(registerApi).register(any())
        order.verify(view).setRegisterError(error)
        order.verify(view).setSubmitEnabled(true)
        order.verifyNoMoreInteractions()
    }
}