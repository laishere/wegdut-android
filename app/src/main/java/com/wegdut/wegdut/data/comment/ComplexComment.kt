package com.wegdut.wegdut.data.comment

data class ComplexComment(
    val comment: Comment,
    val reply: MutableList<Comment>,
    var replyCount: Int,
    var validCount: Int
)