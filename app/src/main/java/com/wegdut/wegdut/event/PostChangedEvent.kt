package com.wegdut.wegdut.event

import com.wegdut.wegdut.data.post.Post

data class PostChangedEvent(
    val post: Post
)