package com.wegdut.wegdut.data.post

import com.wegdut.wegdut.api.LikeApi
import com.wegdut.wegdut.api.PostApi
import com.wegdut.wegdut.data.ContentType
import com.wegdut.wegdut.data.scrollx.ScrollXRequest
import com.wegdut.wegdut.data.scrollx.ScrollXResponse
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor() : PostRepository() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var likeApi: LikeApi


    override fun toggleLike(post: Post): List<Post> {
        val like = !post.iLike
        likeApi.like(ContentType.POST, post.id, like).extract()
        post.iLike = like
        post.like += if (like) 1 else -1
        post.iLikeAnimation = true
        return items
    }

    override fun delete(item: Post) {
        postApi.delete(item.id).extract()
        super.delete(item)
    }

    override fun canSendPost() {
        postApi.canSendPost().extract()
    }

    override fun getAnchor(item: Post?): String? {
        if (item == null) return null
        return item.id.toString()
    }

    override fun getFromApi(request: ScrollXRequest): ScrollXResponse<Post> {
        return postApi.getLatest(request).extract()!!.map { it.toPost() }
    }

    override fun getFromLocal(limit: Int): List<Post> {
        return emptyList()
    }

    override fun getItemOldPosition(item: Post): Int {
        return cache.indexOfFirst { it.id == item.id }
    }
}