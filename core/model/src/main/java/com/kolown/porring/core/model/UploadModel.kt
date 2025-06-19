package com.kolown.porring.core.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadModel(
    val imgUri: String = "",
    val imageRatio: Float = 4f / 5f,
    val description: String = "",
    val categoryItems: List<String> = emptyList()
)
