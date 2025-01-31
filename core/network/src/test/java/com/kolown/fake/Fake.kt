package com.kolown.fake

import kotlinx.serialization.Serializable
import org.junit.Ignore

@Ignore("test instance")
@Serializable
data class Fake(
    val id: Int,
    val name: String,
    val age: Int,
    val list: List<Dummy>,
) {
    @Serializable
    data class Dummy(
        val id: Int,
        val name: String,
    )
}
