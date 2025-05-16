package com.kolown.porring.core.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "follower")
data class FollowerEntity(
    @PrimaryKey
    @ColumnInfo("follower_id")
    val followerId: String,
    @ColumnInfo("name")
    val name: String,
)
