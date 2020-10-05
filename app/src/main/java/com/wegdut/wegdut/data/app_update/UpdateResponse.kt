package com.wegdut.wegdut.data.app_update

data class UpdateResponse(
    val isLatest: Boolean,
    val message: String?,
    val url: String?,
    val notifyUpdate: Boolean,
    val forceUpdate: Boolean
)