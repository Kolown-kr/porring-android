package com.kolown.porring.core.ui.component

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.component.button.PorringIconButton
import com.kolown.porring.core.designsystem.ui.theme.BackgroundDark

@Composable
fun FullscreenImageViewer(
    imageUrl: String = "",
    imageRatio: Float = 4f / 5f,
    onDismiss: () -> Unit = {},
) {
    val isHorizontalImage = imageRatio > 1f
    val imageRotation = if (isHorizontalImage) 90f else 0f
    val appBarAlignment = if (isHorizontalImage) Alignment.BottomCenter else Alignment.TopCenter

    val minScale = if (isHorizontalImage) 1.25f else 1f
    val maxScale = 2f

    var scale by remember { mutableFloatStateOf(minScale) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }


    FullScreenEffect()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .onSizeChanged { boxSize = it }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(minScale, maxScale)

                        val maxOffset = calcMaxOffset(
                            isHorizontalImage = isHorizontalImage,
                            scale = scale,
                            boxSize = boxSize,
                            imageSize = imageSize
                        )

                        offset = Offset(
                            x = (offset.x + pan.x).coerceIn(-maxOffset.x, maxOffset.x),
                            y = (offset.y + pan.y).coerceIn(-maxOffset.y, maxOffset.y)
                        )
                    }
                }
        ) {
            CoilImage(
                modifier = Modifier
                    .align(Alignment.Center)
                    .onSizeChanged { imageSize = it }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y,
                        rotationZ = imageRotation
                    ),
                imageUrl = imageUrl,
                imageRatio = imageRatio,
                isRipple = false
            )
        }

        PorringTopAppBar(
            modifier = Modifier.align(appBarAlignment),
            trailingIcon = {
                PorringIconButton(
                    icon = Icons.Default.Close,
                    onClick = onDismiss,
                    contentDescription = null,
                    color = Color.White
                )
            }
        )
    }
}

private fun calcMaxOffset(
    isHorizontalImage: Boolean,
    scale: Float,
    boxSize: IntSize,
    imageSize: IntSize
): Offset {
    val scaledImageWidth =
        if (isHorizontalImage) imageSize.height * scale else imageSize.width * scale
    val scaledImageHeight =
        if (isHorizontalImage) imageSize.width * scale else imageSize.height * scale

    val maxOffsetX = ((scaledImageWidth - boxSize.width) / 2f).coerceAtLeast(0f)
    val maxOffsetY = ((scaledImageHeight - boxSize.height) / 2f).coerceAtLeast(0f)

    return Offset(maxOffsetX, maxOffsetY)
}

@Composable
private fun FullScreenEffect() {
    val content = LocalContext.current
    val activity = content as? Activity ?: return

    DisposableEffect(activity) {
        val window = activity.window
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)

        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
private fun FocusScreenPreview() {
    FullscreenImageViewer()
}