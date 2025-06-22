package com.kolown.porring.core.model

import java.time.ZonedDateTime

data class User(
    val userId: String = "",
    val email: String = "",
    val receiverEmail: String = email,
    val createAt: ZonedDateTime = ZonedDateTime.now(),
)
