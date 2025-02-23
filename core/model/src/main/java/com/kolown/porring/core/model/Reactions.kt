package com.kolown.porring.core.model

import com.kolown.porring.core.model.Reactions.entries

enum class Reactions(val value: Int) {
    LOVE(0), SURPRISE(1), SMILE(2), STAR(3), THUMB(4);
}

fun Int.toReactions(): Reactions? {
    return entries.firstOrNull { it.value == this }
}