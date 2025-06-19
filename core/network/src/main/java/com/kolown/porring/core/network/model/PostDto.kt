package com.kolown.porring.core.network.model

import com.kolown.porring.core.model.PostModel
import com.kolown.porring.core.network.Util.randomValue

data class PostDto(
    val postId: String = "",
    val authorId: String = "",
    val imageUrl: String = "",
    val imageRatio: Float = 4f / 5f,
    val registerAt: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val reactionCount: List<Int> = emptyList(),
    val myReaction: Int? = null,
    val randomA: Long = randomValue(),
    val randomB: Long = randomValue(),
    val randomC: Long = randomValue(),
    val randomD: Long = randomValue(),
    val randomE: Long = randomValue(),
)

fun PostDto.toPostModel(
    seed: String = "A"
): PostModel {
    return PostModel(
        postId = this.postId,
        authorId = this.authorId,
        imageUrl = this.imageUrl,
        imageRatio = this.imageRatio,
        registerAt = this.registerAt,
        description = this.description,
        tags = this.tags,
        reactions = this.reactionCount.mapIndexedNotNull { index, i -> if (i > 0) index else null },
        myReaction = this.myReaction,
        random = when (seed) {
            "A" -> randomA
            "B" -> randomB
            "C" -> randomC
            "D" -> randomD
            "E" -> randomE
            else -> throw IllegalArgumentException("존재하지 않는 랜덤 시드")
        }
    )
}
