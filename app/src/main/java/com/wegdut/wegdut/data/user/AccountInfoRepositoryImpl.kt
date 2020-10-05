package com.wegdut.wegdut.data.user

import com.wegdut.wegdut.MyApplication
import com.wegdut.wegdut.api.AccountInfoApi
import com.wegdut.wegdut.event.UserModificationEvent
import com.wegdut.wegdut.utils.ApiUtils.extract
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class AccountInfoRepositoryImpl @Inject constructor() : AccountInfoRepository {

    @Inject
    lateinit var api: AccountInfoApi

    override fun modify(dto: UserModificationDto) {
        val user = api.modifyUser(dto).extract().toUser()
        MyApplication.user = user
        EventBus.getDefault().post(UserModificationEvent())
    }
}