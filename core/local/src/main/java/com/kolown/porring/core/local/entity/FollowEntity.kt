package com.kolown.porring.core.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "follow")
data class FollowEntity(
    @PrimaryKey
    val id: String,
    val name: String,
)
