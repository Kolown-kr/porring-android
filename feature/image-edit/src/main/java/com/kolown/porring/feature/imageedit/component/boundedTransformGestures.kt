package com.kolown.porring.feature.imageedit.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

internal fun Modifier.boundedTransformGestures(
    coroutineScope: CoroutineScope,
    scale: Animatable<Float, AnimationVector1D>,
    offsetX: Animatable<Float, AnimationVector1D>,
    offsetY: Animatable<Float, AnimationVector1D>,
    minScale: Float,
    boxSize: Size,
    imageSize: Size
): Modifier = this.pointerInput(boxSize, imageSize, minScale) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()

            if (event.type == PointerEventType.Release) {
                coroutineScope.launch {
                    if (scale.value < minScale) {
                        val scaleJob = async {
                            scale.animateTo(
                                minScale,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                            )
                        }
                        val offsetXJob = async {
                            offsetX.animateTo(
                                0f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                            )
                        }
                        val offsetYJob = async {
                            offsetY.animateTo(
                                0f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                            )
                        }

                        awaitAll(scaleJob, offsetXJob, offsetYJob)
                    }

                    if (scale.value > 5f) {
                        scale.animateTo(
                            5f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                        )
                    }

                    val boundedOffset = getBoundedOffset(
                        scale.value,
                        boxSize,
                        imageSize,
                        Offset(offsetX.value, offsetY.value)
                    )

                    val offsetXJob = async {
                        offsetX.animateTo(
                            boundedOffset.x,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                        )
                    }

                    val offsetYJob = async {
                        offsetY.animateTo(
                            boundedOffset.y,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                        )
                    }

                    awaitAll(offsetXJob, offsetYJob)
                }
            }
        }
    }
}

private fun getBoundedOffset(
    scale: Float,
    boxSize: Size,
    imageSize: Size,
    currentOffset: Offset
): Offset {
    val scaledWidth = imageSize.width * scale
    val scaledHeight = imageSize.height * scale

    val maxOffsetX = ((scaledWidth - boxSize.width) / 2f).coerceAtLeast(0f)
    val maxOffsetY = ((scaledHeight - boxSize.height) / 2f).coerceAtLeast(0f)

    return Offset(
        currentOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
        currentOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
    )
}