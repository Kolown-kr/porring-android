package com.kolown.porring.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.icon.PorringIcons
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme

@Composable
fun Pill(
    icon: ImageVector = PorringIcons.Default.Empty,
    backgroundColor: Color = PorringTheme.colors.surface,
    iconColor: Color = PorringTheme.colors.onSurface,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val shadowColor = PorringTheme.colors.shadow

    Box(
        modifier = modifier
            .size(32.dp)
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        color = android.graphics.Color.TRANSPARENT
                        setShadowLayer(
                            4.dp.toPx(),
                            2.dp.toPx(),
                            2.dp.toPx(),
                            shadowColor.toArgb()
                        )
                    }
                    canvas.nativeCanvas.drawCircle(
                        center.x,
                        center.y,
                        size.minDimension / 2f,
                        paint
                    )
                }
            }
            .background(
                color = backgroundColor,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.Center),
            tint = iconColor
        )
    }
}

@Composable
fun ToggledPill(
    icon: ImageVector = PorringIcons.Default.Empty,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val bgColor = if (isSelected) PorringTheme.colors.primary else PorringTheme.colors.surface
    val iconColor = if (isSelected) PorringTheme.colors.onPrimary else PorringTheme.colors.tertiary

    Pill(
        icon = icon,
        backgroundColor = bgColor,
        iconColor = iconColor,
        modifier = modifier,
        contentDescription = contentDescription,
    )
}

@Composable
@Preview()
private fun PillPreview() {
    PorringTheme() {
        Pill()
    }
}

@Composable
@Preview()
private fun ToggledPillSelectedPreview() {
    PorringTheme() {
        ToggledPill(
            isSelected = true
        )
    }
}

@Composable
@Preview()
private fun ToggledPillUnselectedPreview() {
    PorringTheme() {
        ToggledPill(
            isSelected = false
        )
    }
}
