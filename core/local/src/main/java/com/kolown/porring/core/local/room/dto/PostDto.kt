package com.kolown.porring.core.local.room.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.kolown.porring.core.local.room.entity.HomeItemPost
import com.kolown.porring.core.local.room.entity.Post

sealed interface PostDto {
    data class HomeItemPostDto(
        @Relation(
            parentColumn = "post_id",
            entityColumn = "post_id"
        )
        val post: Post,
        @Embedded val homeItemPost: HomeItemPost,
    ) : PostDto
}