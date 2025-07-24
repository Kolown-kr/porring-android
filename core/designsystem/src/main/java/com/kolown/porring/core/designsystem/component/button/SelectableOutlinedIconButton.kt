package com.kolown.porring.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
fun PorringSelectableOutlinedIconButton(
    selected: Boolean,
    onClick: () -> Unit,
    defaultIcon: ImageVector,
    selectedIcon: ImageVector? = null,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    val icon = if (selected && selectedIcon != null) selectedIcon else defaultIcon
    val bgColor = if (selected) PorringTheme.colors.primary else Color.Transparent
    val borderColor = if (selected) PorringTheme.colors.primary else PorringTheme.colors.tertiary
    val contentColor =
        if (selected) PorringTheme.colors.onPrimary else PorringTheme.colors.tertiary

    IconButton(
        modifier = modifier
            .size(48.dp)
            .border(1.dp, borderColor, CircleShape)
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