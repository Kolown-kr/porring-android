package com.kolown.porring.core.designsystem.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.icon.PorringIcons
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme

@Composable
fun PorringOutlinedIconButton(
    icon: ImageVector = PorringIcons.Default.Empty,
    onClick: () -> Unit = {},
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
) {
    IconButton(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .border(1.dp, PorringTheme.colors.onBackground, CircleShape)
            .background(Color.Transparent),
        onClick = onClick,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = PorringTheme.colors.onBackground,
            modifier = Modifier.size(24.dp)
        )
    }
}