package com.wegdut.wegdut.data.user

interface AccountInfoRepository {
    fun modify(dto: UserModificationDto)
}