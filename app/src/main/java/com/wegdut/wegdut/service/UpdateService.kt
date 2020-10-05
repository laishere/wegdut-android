package com.wegdut.wegdut.service

import com.wegdut.wegdut.api.UpdateApi
import com.wegdut.wegdut.data.app_update.UpdateRequest
import com.wegdut.wegdut.data.app_update.UpdateResponse
import com.wegdut.wegdut.utils.ApiUtils

class UpdateService {

    private val api = ApiUtils.create(UpdateApi::class.java)

    fun checkUpdate(req: UpdateRequest): UpdateResponse {
        val call = api.checkUpdate(req)
        return ApiUtils.handleResultWrapper(call)!!
    }

}