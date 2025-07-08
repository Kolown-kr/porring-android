package com.kolown.porring.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.kolown.porring.core.designsystem.R

object PorringIcons {
    object Default {
        val Empty @Composable get() = ImageVector.vectorResource(R.drawable.ic_empty)
        val ReactionSmile @Composable get() = ImageVector.vectorResource(R.drawable.ic_reaction_smile)
        val ReactionCool @Composable get() = ImageVector.vectorResource(R.drawable.ic_reaction_cool)
        val ReactionMove @Composable get() = ImageVector.vectorResource(R.drawable.ic_reaction_move)
        val ReactionSurprise @Composable get() = ImageVector.vectorResource(R.drawable.ic_reaction_surprise)
        val ReactionWink @Composable get() = ImageVector.vectorResource(R.drawable.ic_reaction_wink)
        val ReactionHeart @Composable get() = ImageVector.vectorResource(R.drawable.ic_reaction_heart)
    }
}