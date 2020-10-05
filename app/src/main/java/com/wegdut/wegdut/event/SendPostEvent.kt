package com.wegdut.wegdut.event

import com.wegdut.wegdut.data.post.Post

data class SendPostEvent(
    val post: Post
)