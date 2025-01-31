package com.kolown.porring.core.network.model

import com.kolown.porring.core.model.ReactionModel
import com.kolown.porring.core.model.toReactions

data class ReactionDto(
    val reactionId: String = "",
    val userId: String = "",
    val postId: String = "",
    val reaction: Int? = null
)

fun ReactionDto.toReactionModel() = ReactionModel(
    userId = userId,
    postId = postId,
    reaction = reaction?.toReactions()
)
