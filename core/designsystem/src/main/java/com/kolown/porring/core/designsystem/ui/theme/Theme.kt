package com.kolown.porring.core.designsystem.ui.theme

import android.app.Activity
import android.os.Build
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun PorringTheme(
    currentRoute: String = "",
    content: @Composable () -> Unit,
) {
    val isForcedDarkMode = currentRoute.shouldForceDarkMode()
    val isDarkMode = if (isForcedDarkMode) true else isSystemInDarkTheme()

    val colors: PorringColor = if (isDarkMode) PorringDarkColor else PorringLightColor
    val isLightSystemBars = isDarkMode.not()

    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    val insetsController = window?.let { WindowCompat.getInsetsController(window, view) }

    SideEffect {
        window?.let {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.setSystemBarBackground()
        }
    }

    LaunchedEffect(isLightSystemBars) {
        insetsController?.isAppearanceLightStatusBars = isLightSystemBars
        insetsController?.isAppearanceLightNavigationBars = isLightSystemBars
    }

    CompositionLocalProvider(
        LocalColor provides colors,
        LocalTypography provides PorringTypography,
        content = content
    )
}

private fun Window.setSystemBarBackground() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this.isNavigationBarContrastEnforced = false
    }

    this.statusBarColor = Color.Transparent.toArgb()
    this.navigationBarColor = Color.Transparent.toArgb()
}

private fun String.shouldForceDarkMode(): Boolean {
    return when {
        this.startsWith("Detail") -> true
        this.startsWith("Camera") -> true
        else -> false
    }
}

object PorringTheme {
    val colors: PorringColor
        @Composable get() = LocalColor.current
    val typography: PorringTypography
        @Composable get() = LocalTypography.current
}