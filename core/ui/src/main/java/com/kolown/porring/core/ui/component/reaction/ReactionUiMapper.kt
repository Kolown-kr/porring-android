package com.kolown.porring.core.ui.component.reaction

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.kolown.porring.core.designsystem.icon.PorringIcons
import com.kolown.porring.core.model.Reaction

@Composable
fun Reaction?.getIcon(): ImageVector {
    return when (this) {
        Reaction.HEART -> PorringIcons.Default.ReactionHeart
        Reaction.SURPRISE -> PorringIcons.Default.ReactionSurprise
        Reaction.SMILE -> PorringIcons.Default.ReactionSmile
        Reaction.COOL -> PorringIcons.Default.ReactionCool
        Reaction.MOVE -> PorringIcons.Default.ReactionMove
        Reaction.WINK -> PorringIcons.Default.ReactionWink
        null -> PorringIcons.Default.ReactionNone
    }
}