package com.kolown.porring.core.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "other_user_post_info",
    primaryKeys = ["post_id"]
)
data class OtherUserPostInfo(
    @ColumnInfo("post_id")
    val postId: String,
    @ColumnInfo("author_id") val authorId: String,
    @ColumnInfo("is_follower") val isFollower: Boolean,
    @ColumnInfo("my_reaction") val myReaction: Int?
)

