package com.wegdut.wegdut.data.send_post

import com.wegdut.wegdut.data.post.Post

interface SendPostRepository {
    fun canSendPost()
    fun sendPost(dto: SendPostDto): Post
}