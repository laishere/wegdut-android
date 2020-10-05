package com.wegdut.wegdut.data.user

data class UserDto(
    var id: Long? = null,
    var name: String? = null,
    var nickname: String? = null,
    var icon: String? = null
) {
    fun toUser() = User(id!!, name!!, nickname!!, icon!!, 0)
}