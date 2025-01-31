package com.kolown.porring.core.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadModel(
    val imgUri: String,
    val description: String,
    val categoryItems: List<String>
)
