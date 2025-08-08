package com.kolown.porring.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme

@Composable
fun PorringSelectableIconButton(
    selected: Boolean,
    onClick: () -> Unit,
    defaultIcon: ImageVector,
    selectedIcon: ImageVector? = null,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    val icon = if (selected && selectedIcon != null) selectedIcon else defaultIcon

    IconButton(
        modifier = modifier
            .size(48.dp)
            .background(Color.Transparent),
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (selected) PorringTheme.colors.primary else PorringTheme.colors.onBackground,
            modifier = Modifier.size(24.dp)
        )
    }
}