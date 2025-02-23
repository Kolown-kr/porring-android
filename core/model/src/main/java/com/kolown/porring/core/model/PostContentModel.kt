package com.kolown.porring.core.model

import kotlinx.serialization.Serializable

@Serializable
data class PostContentModel(
    val postId: String,
    val authorId: String,
    val imageUrl: String,
    val registerAt: String,
    val description: String,
    val tags: List<String>,
    val isFollower: Boolean,
    val reactions: List<Reactions>,
    val myReaction: Reactions? = null,
) {
    companion object {
        val EMPTY = PostContentModel("", "", "", "", "", emptyList(), true, listOf(Reactions.LOVE, Reactions.SMILE), Reactions.LOVE)
    }
}