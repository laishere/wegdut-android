package com.wegdut.wegdut.data

abstract class BaseDiffData {
    abstract var id: Long

    open fun isSame(other: BaseDiffData) = id == other.id

    /**
     * 检查ID无关的item是否具有相同的UI呈现
     */
    open fun hasSameContent(other: BaseDiffData): Boolean {
        val tmp = other.id
        other.id = id
        val eq = this == other
        other.id = tmp
        return eq
    }
}