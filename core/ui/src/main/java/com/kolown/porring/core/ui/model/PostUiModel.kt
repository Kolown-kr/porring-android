package com.kolown.porring.core.ui.model

import com.kolown.porring.core.model.Reaction

data class PostUiModel(
    val postId: String,
    val authorId: String,
    val imageUrl: String,
    val imageRatio: Float,
    val registerAt: String,
    val description: String,
    val tags: List<String>,
    val isFollowing: Boolean,
    val reactions: List<Reaction>,
    val myReaction: Reaction? = null,
) {
    companion object {
        val EMPTY = PostUiModel(
            postId = "",
            authorId = "",
            imageUrl = "",
            imageRatio = 4f / 5f,
            registerAt = "",
            description = "",
            tags = emptyList(),
            isFollowing = true,
            reactions = listOf(Reaction.HEART, Reaction.SMILE),
            myReaction = Reaction.HEART
        )
    }
}