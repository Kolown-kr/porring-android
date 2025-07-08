package com.kolown.porring.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kolown.porring.core.model.Reaction

@Composable
fun ReactionGroup(
    reactions: List<Reaction>,
    myReaction: Reaction? = null,
    modifier: Modifier = Modifier,
) {
    val reactionList = if (myReaction != null) {
        listOf(myReaction) + reactions.filter { it != myReaction }.sortedBy { it.ordinal }
    } else {
        reactions.sortedBy { it.ordinal }
    }

    if (reactionList.isNotEmpty()) {
        Row(
            modifier = modifier,
        ) {
            reactionList.forEachIndexed { index, reaction ->
                val isMyReaction = myReaction != null && index == 0
                ReactionPill(
                    reaction = reaction,
                    isSelected = isMyReaction,
                    modifier = Modifier
                        .offset(x = (-16 * index).dp)
                        .zIndex((reactionList.size - index).toFloat())
                )
            }
        }
    }
}