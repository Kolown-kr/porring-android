package com.kolown.porring.core.model

import com.kolown.porring.core.model.Reaction.entries

enum class Reaction(val value: Int) {
    LOVE(0), SURPRISE(1), SMILE(2), STAR(3), THUMB(4);
}

fun Int.toReactions(): Reaction? {
    return entries.firstOrNull { it.value == this }
}