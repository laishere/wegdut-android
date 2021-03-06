package com.wegdut.wegdut

import android.content.Context
import android.content.Intent
import androidx.multidex.MultiDex
import com.tencent.bugly.crashreport.CrashReport
import com.wegdut.wegdut.config.Config
import com.wegdut.wegdut.data.user.LoginRepository
import com.wegdut.wegdut.data.user.User
import com.wegdut.wegdut.di.DaggerAppComponent
import com.wegdut.wegdut.utils.ApiUtils
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyApplication : DaggerApplication() {

    @Inject
    lateinit var loginRepository: LoginRepository

    companion object {
        private const val userLock = 1
        private var realUser: User? = null
        var user: User?
            get() = synchronized(userLock) { realUser }
            set(value) {
                synchronized(userLock) {
                    realUser = value
                }
            }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }

    override fun onCreate() {
        ApiUtils.initApi(this)
        super.onCreate()
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(applicationContext, Config.buglyAppId, BuildConfig.DEBUG)
        }
        loadUser()
        startService(Intent(this, CourseService::class.java))
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun loadUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                loginRepository.getCurrentUser()
            } catch (e: Throwable) {
            }
        }
    }


}