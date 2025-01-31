package com.kolown.fake

import kotlinx.serialization.Serializable

@Serializable
data class Fakes(
    val users: List<User>
) {
    @Serializable
    data class User(
        val firstName: String,
        val homepage: String,
        val lastName: String,
        val phoneNumber: String,
        val userId: Int
    )
}