package com.kolown.porring.core.model

data class User(
    val userId: String = "",
    val email: String = "",
    val follows: List<Follow> = emptyList(),
)
