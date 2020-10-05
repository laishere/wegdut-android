package com.wegdut.wegdut.data.sts

import java.util.*

data class STSDto(
    val accessKeyId: String,
    val accessKeySecret: String,
    val expiration: Date,
    val securityToken: String
)