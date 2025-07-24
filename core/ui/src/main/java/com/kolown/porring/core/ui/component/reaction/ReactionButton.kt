package com.kolown.porring.core.ui.component.reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme

@Composable
internal fun ReactionButton(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (selected) PorringTheme.colors.primary else Color.Transparent
    val contentColor =
        if (selected) PorringTheme.colors.onPrimary else PorringTheme.colors.primary

    IconButton(
        modifier = modifier
            .size(48.dp)
            .background(bgColor, CircleShape),
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
    }
}