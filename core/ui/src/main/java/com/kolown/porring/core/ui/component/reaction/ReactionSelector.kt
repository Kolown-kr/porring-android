package com.kolown.porring.core.ui.component.reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.model.Reaction

@Composable
fun ReactionSelector(
    modifier: Modifier = Modifier,
    selectedReaction: Reaction? = null,
    onReactionClick: (Reaction) -> Unit = {},
    onDismiss: () -> Unit,
) {
    val shadowColor = PorringTheme.colors.shadow

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
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
                    canvas.nativeCanvas.drawRoundRect(
                        0f,
                        0f,
                        size.width,
                        size.height,
                        24.dp.toPx(),
                        24.dp.toPx(),
                        paint
                    )
                }
            }
            .background(
                color = PorringTheme.colors.surface,
                shape = RoundedCornerShape(24.dp),
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Reaction.entries.forEach {
                ReactionButton(
                    selected = selectedReaction == it,
                    onClick = {
                        onReactionClick(it)
                        onDismiss()
                    },
                    icon = it.getIcon(),
                    contentDescription = it.name
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ReactionSelector { }
}