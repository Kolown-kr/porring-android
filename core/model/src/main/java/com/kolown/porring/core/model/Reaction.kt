package com.kolown.porring.core.model

import com.kolown.porring.core.model.Reaction.entries

enum class Reaction(val value: Int) {
    HEART(0), SURPRISE(1), SMILE(2), COOL(3), MOVE(4), WINK(5);
}

fun Int.toReactions(): Reaction? {
    return entries.firstOrNull { it.value == this }
}