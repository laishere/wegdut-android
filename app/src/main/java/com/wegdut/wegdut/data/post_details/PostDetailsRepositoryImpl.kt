package com.wegdut.wegdut.data.post_details

import com.wegdut.wegdut.api.PostApi
import com.wegdut.wegdut.data.comment.ComplexComment
import com.wegdut.wegdut.data.comment.PostCommentDto
import com.wegdut.wegdut.data.embedded_comment.CommonEmbeddedCommentRepository
import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.utils.ApiUtils.extract
import javax.inject.Inject

class PostDetailsRepositoryImpl @Inject constructor() : CommonEmbeddedCommentRepository<Post>() {

    @Inject
    lateinit var postApi: PostApi

    override fun getContent(): Post {
        return postApi.getPost(contentId).extract().toPost()
    }

    override fun reply(parent: Post, dto: PostCommentDto): List<ComplexComment> {
        val posted = commentApi.postComment(contentType, parent.id, dto).extract()
        parent.comment++
        parent.iComment = true
        parent.iCommentAnimation = true
        add(posted.toComplexComment())
        return items
    }

    override fun toggleLike(content: Post) {
        val like = !content.iLike
        likeApi.like(contentType, contentId, like).extract()
        content.iLike = like
        content.iLikeAnimation = true
        content.like += if (like) 1 else -1
    }
}