package com.kolown.porring.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kolown.porring.core.designsystem.ui.theme.PrimaryUnActive
import com.kolown.porring.core.model.Reaction
import com.kolown.porring.core.ui.R

@Composable
fun ReactionDialog(
    modifier: Modifier = Modifier,
    activatedReaction: Reaction? = null,
    selectedReaction: (Reaction) -> Unit = {},
    onDismiss: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Reaction.entries.forEach {
                ReactionButton(
                    didIReact = activatedReaction == it,
                    reaction = it,
                    onClick = { reaction ->
                        selectedReaction(reaction)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun ReactionButton(
    didIReact: Boolean,
    reaction: Reaction,
    onClick: (Reaction) -> Unit,
) {
    IconButton(
        modifier = Modifier
            .size(30.dp)
            .background(
                color = if (didIReact) PrimaryUnActive else Color.Transparent,
                shape = CircleShape
            ),
        onClick = { onClick(reaction) }
    ) {
        AsyncImage(
            modifier = Modifier.size(30.dp),
            model = reaction.toImage(),
            contentDescription = null
        )
    }

}

fun Reaction.toImage() =
    when (this) {
        Reaction.HEART -> R.drawable.ic_reaction_1
        Reaction.SURPRISE -> R.drawable.ic_reaction_2
        Reaction.SMILE -> R.drawable.ic_reaction_3
        Reaction.COOL -> R.drawable.ic_reaction_4
        Reaction.MOVE -> R.drawable.ic_reaction_5
        Reaction.WINK -> R.drawable.ic_reaction_1
    }

@Preview
@Composable
private fun Preview() {
    ReactionDialog { }
}