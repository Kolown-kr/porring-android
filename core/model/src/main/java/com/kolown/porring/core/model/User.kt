package com.kolown.porring.core.model

data class User(
    val userId: String = "",
    val email: String = "",
    val followers: List<Follower> = emptyList(),
)
