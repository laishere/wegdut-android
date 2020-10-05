package com.wegdut.wegdut.data

data class PageWrapper<T>(
    val list: List<T>,
    val isLast: Boolean,
    val marker: String? = null
) {
    fun <R> map(mapper: (T) -> R) = PageWrapper(
        list.map(mapper), isLast, marker
    )
}