package com.wegdut.wegdut.api

import com.wegdut.wegdut.data.ResultWrapper
import com.wegdut.wegdut.data.app_update.UpdateRequest
import com.wegdut.wegdut.data.app_update.UpdateResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UpdateApi {
    @POST("/check_update/v2")
    fun checkUpdate(@Body updateRequest: UpdateRequest): Call<ResultWrapper<UpdateResponse?>>
}