package com.kolown.porring.core.ui.model

import com.kolown.porring.core.model.Reaction

data class PostUiModel(
    val postId: String,
    val authorId: String,
    val imageUrl: String,
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
            registerAt = "",
            description = "",
            tags = emptyList(),
            isFollowing = true,
            reactions = listOf(Reaction.LOVE, Reaction.SMILE),
            myReaction = Reaction.LOVE
        )
    }
}