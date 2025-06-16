package com.kolown.porring.feature.setting.privacy

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.viewinterop.AndroidView
import com.kolown.porring.core.designsystem.R
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTopAppBar

@Composable
internal fun PrivacyRoute(
    popBackStack: () -> Unit = {},
    padding: PaddingValues = PaddingValues()
) {

    PrivacyScreen(
        popBackStack = popBackStack,
        padding = padding
    )
}

@Composable
private fun PrivacyScreen(
    popBackStack: () -> Unit = {},
    padding: PaddingValues = PaddingValues()
) {
    Column(
        modifier = Modifier
            .padding(padding)
    ) {
        PorringTopAppBar(
            navigationIcon = {
                PorringIconButton(
                    icon = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                    onClick = popBackStack,
                    contentDescription = stringResource(com.kolown.porring.feature.setting.R.string.string_back_button)
                )
            }
        )

        AndroidView(
            modifier = Modifier
                .fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    loadUrl("https://www.google.co.kr/")
                }
            }
        )
    }
}