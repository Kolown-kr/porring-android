package com.kolown.porring.core.ui.ext

import android.graphics.BlurMaskFilter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.shadow(
    color: Color = Color.Black,
    blur: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    shape: Shape = RectangleShape,
    modifier: Modifier = Modifier,
) = this.then(
    modifier.drawBehind {
        val path = when (val outline = shape.createOutline(size, layoutDirection, this)) {
            is Outline.Generic -> outline.path
            is Outline.Rectangle -> Path().apply { addRect(outline.rect) }
            is Outline.Rounded -> Path().apply { addRoundRect(outline.roundRect) }
        }

        this.drawIntoCanvas { canvas ->
            val paint = Paint()
            val nativePaint = paint.asFrameworkPaint()

            nativePaint.maskFilter = BlurMaskFilter(blur.toPx(), BlurMaskFilter.Blur.NORMAL)
            nativePaint.color = color.toArgb()

            canvas.translate(offsetX.toPx(), offsetY.toPx())
            canvas.drawPath(path, paint)
        }
    }
)
