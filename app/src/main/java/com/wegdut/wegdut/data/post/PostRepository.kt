package com.wegdut.wegdut.data.post

import com.wegdut.wegdut.scrollx.BaseScrollXRepository

abstract class PostRepository : BaseScrollXRepository<Post>() {
    abstract fun toggleLike(post: Post): List<Post>
    abstract fun canSendPost()
}