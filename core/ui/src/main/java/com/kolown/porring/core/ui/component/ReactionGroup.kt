package com.kolown.porring.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.model.Reaction
import com.kolown.porring.core.ui.ext.shadow

@Composable
fun ReactionGroup(
    reactions: List<Reaction>,
    myReaction: Reaction? = null,
    modifier: Modifier = Modifier,
) {
    val reactionList = reactions
        .sortedBy { it.ordinal }
        .toMutableList()
        .apply { myReaction?.let { add(0, myReaction) } }
        .distinct()

    if (reactionList.isNotEmpty()) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy((-15).dp)
        ) {
            reactionList.forEachIndexed { index, reaction ->
                if (myReaction != null && index == 0) {
                    ReactionIcons(
                        index = index,
                        reaction = reaction,
                        color = Primary
                    )
                } else {
                    ReactionIcons(index, reaction)
                }
            }
        }
    }
}

@Composable
private fun ReactionIcons(
    index: Int,
    reaction: Reaction,
    color: Color = Color.White
) {

    Card(
        modifier = Modifier
            .size(24.dp)
            .zIndex(-index.toFloat())
            .shadow(
                color = Color.Black.copy(0.25f),
                shape = CircleShape,
                offsetX = 2.dp,
                offsetY = 1.dp,
                blur = 4.dp
            )
            .background(
                color = color,
                shape = CircleShape
            )
            .padding(4.dp),
    ) {
        AsyncImage(
            model = reaction.toImage(),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
    }
}
