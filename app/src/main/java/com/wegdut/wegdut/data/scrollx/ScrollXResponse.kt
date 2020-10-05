package com.wegdut.wegdut.data.scrollx

data class ScrollXResponse<T>(
    val topList: List<T>,
    val isTopDone: Boolean,
    val bottomList: List<T>,
    val isBottomDone: Boolean
) {
    fun <R> map(mapper: (T) -> R): ScrollXResponse<R> {
        return ScrollXResponse(
            topList.map(mapper),
            isTopDone,
            bottomList.map(mapper),
            isBottomDone
        )
    }
}