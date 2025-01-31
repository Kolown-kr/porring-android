package com.kolown.porring.core.network.model

import com.kolown.porring.core.model.TagModel

data class TagDto(
    val tagId: String = "",
    val tagName: String = ""
)

fun TagDto.toTagModel() = TagModel(
    tagName = tagName
)
