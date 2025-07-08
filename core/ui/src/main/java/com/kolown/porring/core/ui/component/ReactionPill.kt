package com.kolown.porring.core.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kolown.porring.core.designsystem.component.ToggledPill
import com.kolown.porring.core.designsystem.icon.PorringIcons
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.model.Reaction

@Composable
fun ReactionPill(
    reaction: Reaction,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val icon = when (reaction) {
        Reaction.HEART -> PorringIcons.Default.ReactionHeart
        Reaction.SMILE -> PorringIcons.Default.ReactionSmile
        Reaction.SURPRISE -> PorringIcons.Default.ReactionSurprise
        Reaction.COOL -> PorringIcons.Default.ReactionCool
        Reaction.MOVE -> PorringIcons.Default.ReactionMove
        Reaction.WINK -> PorringIcons.Default.ReactionWink
    }

    ToggledPill(
        icon = icon,
        isSelected = isSelected,
        modifier = modifier,
    )
}

@Composable
@Preview(showBackground = true)
private fun PreviewReactionFill() {
    PorringTheme {
        Row {
            ReactionPill(reaction = Reaction.HEART, isSelected = true)
            ReactionPill(reaction = Reaction.HEART, isSelected = false)
        }
    }
}
