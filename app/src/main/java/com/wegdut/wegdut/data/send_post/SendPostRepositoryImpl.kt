package com.wegdut.wegdut.data.send_post

import com.wegdut.wegdut.api.PostApi
import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class SendPostRepositoryImpl @Inject constructor() : SendPostRepository {

    @Inject
    lateinit var postApi: PostApi

    override fun canSendPost() {
        postApi.canSendPost().extract()
    }

    override fun sendPost(dto: SendPostDto): Post {
        return postApi.sendPost(dto).extract()!!.toPost()
    }
}