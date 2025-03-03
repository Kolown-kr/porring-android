package com.kolown.porring.feature.imageedit

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kolown.porring.core.designsystem.R
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.ui.R.drawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
internal fun ImageEditRoute(
    imgUri: String = "",
    padding: PaddingValues = PaddingValues(),
    navigateToUpload: (String) -> Unit = {}
) {
    var cropRatio by remember { mutableStateOf(CropRatio.PORTRAIT) }
    val boxSize = remember { mutableStateOf(Size.Zero) }
    val imageSize = remember { mutableStateOf(Size.Zero) }

    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val coroutineScope = rememberCoroutineScope()

    fun getBoundedOffset(scale: Float, offset: Float, boxSize: Float, imageSize: Float): Float {
        val maxOffset = maxOf(((imageSize * scale) - boxSize) / 2, 0f)
        return offset.coerceIn(-maxOffset, maxOffset)
    }

    ImageEditScreen(
        padding = padding,
        imgUri = imgUri,
        boxSize = boxSize.value,
        imageSize = imageSize.value,
        cropRatio = cropRatio,
        coroutineScope = coroutineScope,
        scale = scale,
        offsetX = offsetX,
        offsetY = offsetY,
        navigateToUpload = { navigateToUpload("") },
        updateBoxSize = { boxSize.value = it },
        updateImageSize = { imageSize.value = it },
        updateCropRatio = { cropRatio = it },
        getBoundedOffset = { _, _, box, image ->
            getBoundedOffset(
                scale.value,
                offsetX.value,
                box,
                image
            )
        }
    )
}

@Composable
private fun ImageEditScreen(
    padding: PaddingValues = PaddingValues(),
    imgUri: String = "",
    boxSize: Size = Size.Zero,
    imageSize: Size = Size.Zero,
    cropRatio: CropRatio = CropRatio.PORTRAIT,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    scale: Animatable<Float, AnimationVector1D> = Animatable(1f),
    offsetX: Animatable<Float, AnimationVector1D> = Animatable(0f),
    offsetY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    navigateToUpload: () -> Unit = {},
    updateBoxSize: (Size) -> Unit = { },
    updateImageSize: (Size) -> Unit = { },
    updateCropRatio: (CropRatio) -> Unit = { },
    getBoundedOffset: (Float, Float, Float, Float) -> Float = { _, _, _, _ -> 0f }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        PorringTopAppBar(
            navigationIcon = {
                PorringIconButton(
                    icon = ImageVector.vectorResource(drawable.ic_close),
                    onClick = {
                        // 뒤로가기
                    },
                    contentDescription = stringResource(R.string.string_go_back)
                )
            },
            trailingIcon = {
                PorringIconButton(
                    icon = ImageVector.vectorResource(drawable.ic_arrow_forward),
                    onClick = navigateToUpload,
                    contentDescription = stringResource(R.string.string_upload_image)
                )
            }
        )

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .aspectRatio(CropRatio.PORTRAIT.ratio)
                .onSizeChanged { size ->
                    updateBoxSize(Size(size.width.toFloat(), size.height.toFloat()))
                }
                .clip(RectangleShape)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        coroutineScope.launch {
                            scale.snapTo((scale.value * zoom).coerceIn(0.5f, 5f))
                            offsetX.snapTo(offsetX.value + pan.x)
                            offsetY.snapTo(offsetY.value + pan.y)
                        }
                    }
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()

                            if (event.type == PointerEventType.Release) {
                                coroutineScope.launch {
                                    if (scale.value < 1f) {
                                        val scaleJob = async {
                                            scale.animateTo(
                                                1f,
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

                                    val boundedX = getBoundedOffset(
                                        scale.value,
                                        offsetX.value,
                                        boxSize.width,
                                        imageSize.width
                                    )
                                    val boundedY = getBoundedOffset(
                                        scale.value,
                                        offsetY.value,
                                        boxSize.height,
                                        imageSize.height
                                    )

                                    val offsetXJob = if (boundedX != offsetX.value) {
                                        async {
                                            offsetX.animateTo(
                                                boundedX,
                                                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                                            )
                                        }
                                    } else null

                                    val offsetYJob = if (boundedY != offsetY.value) {
                                        async {
                                            offsetY.animateTo(
                                                boundedY,
                                                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                                            )
                                        }
                                    } else null

                                    awaitAll(
                                        *(listOfNotNull(
                                            offsetXJob,
                                            offsetYJob
                                        ).toTypedArray())
                                    )
                                }
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .onSizeChanged { size ->
                        updateImageSize(Size(size.width.toFloat(), size.height.toFloat()))
                    }
                    .graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value,
                        translationX = offsetX.value,
                        translationY = offsetY.value
                    ),
                model = imgUri,
                contentDescription = "Selected Image"
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(cropRatio.ratio)
                    .border(4.dp, Color.Red)
            )
        }

        ButtonGroup(updateCropRatio = updateCropRatio)
    }
}

@Composable
private fun ButtonGroup(
    modifier: Modifier = Modifier,
    updateCropRatio: (CropRatio) -> Unit = { }
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(40.dp),
                onClick = { updateCropRatio(CropRatio.PORTRAIT) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("4:5 (세로)")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(40.dp),
                onClick = { updateCropRatio(CropRatio.LANDSCAPE) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("5:4 (가로)")
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewImageEditScreen() {
    ImageEditScreen()
}

enum class CropRatio(val ratio: Float) { PORTRAIT(4f / 5f), LANDSCAPE(5f / 4f) }