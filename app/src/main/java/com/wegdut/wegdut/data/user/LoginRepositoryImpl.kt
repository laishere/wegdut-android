package com.wegdut.wegdut.data.user

import com.wegdut.wegdut.MyApplication
import com.wegdut.wegdut.api.LoginApi
import com.wegdut.wegdut.event.LoginEvent
import com.wegdut.wegdut.room.v1.kv_storage.KVDao
import com.wegdut.wegdut.room.v1.kv_storage.KVEntity
import com.wegdut.wegdut.utils.ApiUtils
import com.wegdut.wegdut.utils.ApiUtils.extract
import com.wegdut.wegdut.utils.Utils
import org.greenrobot.eventbus.EventBus
import java.util.*
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor() : LoginRepository {

    @Inject
    lateinit var kvDao: KVDao

    @Inject
    lateinit var api: LoginApi

    companion object {
        const val USER_KEY = "USER_KEY"
    }

    override fun getCurrentUser() {
        val kv = kvDao.get(USER_KEY)
        kv?.let {
            val user = ApiUtils.gson.fromJson(it.value, User::class.java)
            setUser(user)
        }
        getUserFromApi()
    }

    override fun login(username: String, password: String): User {
        val userDto = api.login(username, password).extract()
        val user = userDto.toUser()
        val json = ApiUtils.gson.toJson(user)
        val kv = KVEntity(USER_KEY, json, Date())
        kvDao.save(kv)
        setUser(user)
        return user
    }

    override fun logout() {
        Utils.quiet {
            api.logout().execute()
        }
        kvDao.delete(USER_KEY)
        setUser(null)
    }

    private fun getUserFromApi() {
        val user = api.getCurrentUser().extract()?.toUser()
        val now = Date()
        val kv2 = KVEntity(USER_KEY, ApiUtils.gson.toJson(user), now)
        kvDao.save(kv2)
        setUser(user)
    }

    private fun setUser(u: User?) {
        if (u == MyApplication.user) return
        MyApplication.user = u
        val status =
            if (u != null) LoginEvent.Status.LOGIN
            else LoginEvent.Status.LOGOUT
        EventBus.getDefault().post(LoginEvent(status))
    }
}