package com.kolown.porring.feature.camera.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.kolown.porring.core.designsystem.R.drawable
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.feature.camera.R


@Composable
internal fun CameraRoute(
    navigateToUpload: (String) -> Unit = {},
    padding: PaddingValues = PaddingValues(),
    popBackStack: () -> Unit = {},
) {
    CameraScreen(
        navigateToUpload = navigateToUpload,
        popBackStack = popBackStack,
        padding = padding
    )
}

@Composable
private fun CameraScreen(
    navigateToUpload: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    padding: PaddingValues = PaddingValues(),
) {
    var cameraFlashState by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CameraContent(
            isFlashOn = cameraFlashState,
            navigateToUpload = navigateToUpload,
            padding = padding
        )

        PorringTopAppBar(
            navigationIcon = {
                PorringIconButton(
                    icon = ImageVector.vectorResource(drawable.ic_arrow_back),
                    onClick = popBackStack,
                    contentDescription = "뒤로가기",
                    color = Color.White
                )
            },
            trailingIcon = {
                PorringIconButton(
                    icon = ImageVector.vectorResource(R.drawable.ic_flash),
                    onClick = { cameraFlashState = !cameraFlashState },
                    contentDescription = "플래시 켜기/끄기",
                    color = Color.White
                )
            },
            modifier = Modifier.padding(top = padding.calculateTopPadding())
        )
    }
}


