package com.wegdut.wegdut.ui.post_details

import com.wegdut.wegdut.data.post.Post
import com.wegdut.wegdut.ui.embedded_comment.EmbeddedCommentView
import javax.inject.Inject

class PostDetailsView @Inject constructor() : EmbeddedCommentView<Post>() {
    override fun setContentInfo(content: Post) {
        replyCount = content.comment
        iReply = content.iComment
        hasReplyAnimation = content.iCommentAnimation
        iLike = content.iLike
        hasLikeAnimation = content.iLikeAnimation
        likeCount = content.like
    }

    override fun setContentReplyInfo(content: Post) {
        commentReplyDialog.setReplyIcon(content.user.icon)
        commentReplyDialog.setReplyAbstract(content.getAbstract())
        commentReplyDialog.setHint("回复 ${content.user.nickname}")
    }
}