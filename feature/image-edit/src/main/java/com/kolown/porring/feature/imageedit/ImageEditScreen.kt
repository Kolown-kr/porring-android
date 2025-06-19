package com.kolown.porring.feature.imageedit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kolown.porring.core.designsystem.R
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.ui.R.drawable
import com.kolown.porring.feature.imageedit.component.boundedTransformGestures
import com.kolown.porring.feature.imageedit.model.CropRatio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

@Composable
internal fun ImageEditRoute(
    viewModel: ImageEditViewModel = hiltViewModel(),
    imgUri: String = "",
    padding: PaddingValues = PaddingValues(),
    navigateToHome: () -> Unit = {},
    navigateToUpload: (String, Float) -> Unit = { _, _ -> }
) {
    val editedUri by viewModel.imageUri.collectAsStateWithLifecycle()

    var cropRatio by remember { mutableStateOf(CropRatio.PORTRAIT) }
    var boxSize by remember { mutableStateOf(Size.Zero) }
    var imageSize by remember { mutableStateOf(Size.Zero) }
    var minScale by remember { mutableFloatStateOf(1f) }

    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        navigateToHome()
    }

    LaunchedEffect(editedUri) {
        editedUri?.let { navigateToUpload(it, cropRatio.ratio) }
    }

    ImageEditScreen(
        padding = padding,
        imgUri = imgUri,
        minScale = minScale,
        boxSize = boxSize,
        imageSize = imageSize,
        cropRatio = cropRatio,
        coroutineScope = coroutineScope,
        scale = scale,
        offsetX = offsetX,
        offsetY = offsetY,
        cropImage = {
            viewModel.cropImage(
                imgUri,
                scale.value,
                offsetX.value,
                offsetY.value,
                boxSize,
                imageSize
            )
        },
        navigateToHome = navigateToHome,
        updateBoxSize = { boxSize = it },
        updateImageSize = { imageSize = it },
        updateMinScale = { minScale = it },
        updateCropRatio = { cropRatio = it },
    )
}

@Composable
private fun ImageEditScreen(
    padding: PaddingValues = PaddingValues(),
    imgUri: String = "",
    minScale: Float = 1f,
    boxSize: Size = Size.Zero,
    imageSize: Size = Size.Zero,
    cropRatio: CropRatio = CropRatio.PORTRAIT,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    scale: Animatable<Float, AnimationVector1D> = Animatable(1f),
    offsetX: Animatable<Float, AnimationVector1D> = Animatable(0f),
    offsetY: Animatable<Float, AnimationVector1D> = Animatable(0f),
    cropImage: () -> Unit = {},
    navigateToHome: () -> Unit = {},
    updateBoxSize: (Size) -> Unit = { },
    updateImageSize: (Size) -> Unit = { },
    updateMinScale: (Float) -> Unit = { },
    updateCropRatio: (CropRatio) -> Unit = { }
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
                    onClick = navigateToHome,
                    contentDescription = stringResource(R.string.string_go_back)
                )
            },
            trailingIcon = {
                PorringIconButton(
                    icon = ImageVector.vectorResource(drawable.ic_arrow_forward),
                    onClick = cropImage,
                    contentDescription = stringResource(R.string.string_upload_image)
                )
            }
        )

        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
                .aspectRatio(CropRatio.PORTRAIT.ratio)
                .clip(RectangleShape)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        coroutineScope.launch {
                            scale.snapTo((scale.value * zoom).coerceIn(0.5f, 8f))
                            offsetX.snapTo(offsetX.value + pan.x)
                            offsetY.snapTo(offsetY.value + pan.y)
                        }
                    }
                }
                .boundedTransformGestures(
                    coroutineScope = coroutineScope,
                    scale = scale,
                    offsetX = offsetX,
                    offsetY = offsetY,
                    minScale = minScale,
                    boxSize = boxSize,
                    imageSize = imageSize
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
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

            GridBox(
                cropRatio = cropRatio,
                imageSize = imageSize,
                coroutineScope = coroutineScope,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                updateBoxSize = updateBoxSize,
                updateMinScale = updateMinScale
            )
        }

        ButtonGroup(updateCropRatio = updateCropRatio)
    }
}

@Composable
private fun GridBox(
    cropRatio: CropRatio,
    imageSize: Size,
    coroutineScope: CoroutineScope,
    scale: Animatable<Float, AnimationVector1D>,
    offsetX: Animatable<Float, AnimationVector1D>,
    offsetY: Animatable<Float, AnimationVector1D>,
    updateBoxSize: (Size) -> Unit,
    updateMinScale: (Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(cropRatio.ratio)
            .onSizeChanged { size ->
                updateBoxSize(size.toSize())

                coroutineScope.launch {
                    val computedMinScale = calcMinScale(size.toSize(), imageSize)

                    updateMinScale(computedMinScale)
                    scale.snapTo(computedMinScale)
                    offsetX.snapTo(0f)
                    offsetY.snapTo(0f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = 1.dp.toPx()

            val width = size.width
            val height = size.height

            repeat(4) {
                drawLine(
                    color = Color.White,
                    start = Offset(0f, height / 3 * it),
                    end = Offset(width, height / 3 * it),
                    strokeWidth = strokeWidthPx
                )
            }

            repeat(3 + 1) {
                drawLine(
                    color = Color.White,
                    start = Offset(width / 3 * it, 0f),
                    end = Offset(width / 3 * it, height),
                    strokeWidth = strokeWidthPx
                )
            }

        }
    }
}

@Composable
private fun ButtonGroup(updateCropRatio: (CropRatio) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        CropRatioButton("4:5 (세로)", CropRatio.PORTRAIT, updateCropRatio)
        Spacer(Modifier.width(16.dp))
        CropRatioButton("5:4 (가로)", CropRatio.LANDSCAPE, updateCropRatio)
    }
}

@Composable
private fun CropRatioButton(text: String, ratio: CropRatio, updateCropRatio: (CropRatio) -> Unit) {
    Button(
        modifier = Modifier
            .padding(16.dp)
            .height(40.dp),
        onClick = { updateCropRatio(ratio) },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary)
    ) {
        Text(text)
    }
}

private fun calcMinScale(boxSize: Size, imageSize: Size): Float {
    val (boxW, boxH) = boxSize
    val (imgW, imgH) = imageSize
    return if ((imgW / imgH) > (boxW / boxH)) boxH / imgH else boxW / imgW
}

@Composable
@Preview(showBackground = true)
private fun PreviewImageEditScreen() {
    ImageEditScreen()
}