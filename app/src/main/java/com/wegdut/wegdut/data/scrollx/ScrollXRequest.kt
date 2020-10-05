package com.wegdut.wegdut.data.scrollx

data class ScrollXRequest(
    val topAnchor: String? = null,
    val topOffset: Int,
    val topCount: Int,
    val bottomAnchor: String? = null,
    val bottomOffset: Int,
    val bottomCount: Int
)