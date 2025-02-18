package com.kolown.porring.core.local

import com.kolown.porring.core.local.room.dto.PostDto
import com.kolown.porring.core.local.room.entity.HomeItemPost
import com.kolown.porring.core.local.room.entity.Post
import com.kolown.porring.core.local.room.entity.PostType
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.toReactions

fun PostContentModel.toPostDto(type: PostType): PostDto {
    return when (type) {
        PostType.HOME_ITEM_POST -> this.toHomeItemPostDto()
        else -> throw IllegalArgumentException("Invalid post type")
    }
}

private fun PostContentModel.toHomeItemPostDto() = PostDto.HomeItemPostDto(
    post = this.toPost(PostType.HOME_ITEM_POST),
    homeItemPost = HomeItemPost(
        postId = this.postId,
        authorId = this.authorId,
        isFollower = this.isFollower,
        myReaction = this.myReaction?.value
    )
)

private fun PostContentModel.toPost(type: PostType) = Post(
    id = this.postId,
    imageUrl = this.imageUrl,
    registerAt = this.registerAt,
    description = this.description,
    tags = this.tags,
    reactions = this.reactions.map { it.value },
    type = type
)

fun PostDto.HomeItemPostDto.toPostContentModel() = PostContentModel(
    postId = this.post.id,
    authorId = this.homeItemPost.authorId,
    imageUrl = this.post.imageUrl,
    registerAt = this.post.registerAt,
    description = this.post.description,
    tags = this.post.tags,
    isFollower = this.homeItemPost.isFollower,
    reactions = this.post.reactions.mapNotNull { it.toReactions() },
    myReaction = this.homeItemPost.myReaction?.toReactions(),
)