package com.kolown.porring.core.designsystem.ui.theme

import android.app.Activity
import android.os.Build
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalIsDarkTheme = staticCompositionLocalOf { false }

@Composable
fun PorringTheme(
    isLightBars: Boolean,
    content: @Composable () -> Unit,
) {
    val isDark = LocalIsDarkTheme.current
    val colors: PorringColor = if (isDark) PorringDarkColor else PorringLightColor

    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window
        val insetsController = WindowCompat.getInsetsController(window, view)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        insetsController.isAppearanceLightStatusBars = isLightBars
        insetsController.isAppearanceLightNavigationBars = isLightBars
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()
//            if (isLightBars) Background.toArgb() else BackgroundDark.toArgb()
    }

    CompositionLocalProvider(
        LocalColor provides colors,
        LocalTypography provides PorringTypography,
        content = content
    )
}

object PorringTheme {
    val colors: PorringColor
        @Composable get() = LocalColor.current
    val typography: PorringTypography
        @Composable get() = LocalTypography.current
}