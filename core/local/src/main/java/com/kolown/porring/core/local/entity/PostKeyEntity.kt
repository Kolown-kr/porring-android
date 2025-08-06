package com.kolown.porring.core.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_post_keys")
data class HomePostKeyEntity(
    @PrimaryKey
    @ColumnInfo("post_id") val postId: String,
)

@Entity(tableName = "random_post_keys")
data class RandomPostKeyEntity(
    @PrimaryKey
    @ColumnInfo("post_id") val postId: String,
    @ColumnInfo("sort_order") val sortOrder: Int,
)

@Entity(tableName = "gallery_post_keys")
data class GalleryPostKeyEntity(
    @PrimaryKey
    @ColumnInfo("post_id") val postId: String,
)