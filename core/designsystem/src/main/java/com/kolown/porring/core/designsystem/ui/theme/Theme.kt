package com.kolown.porring.core.designsystem.ui.theme

import android.app.Activity
import android.os.Build
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun PorringTheme(
    content: @Composable () -> Unit,
) {
    val isDark = DarkThemeController.isDark
    val colors = remember(isDark) { if (isDark) PorringDarkColor else PorringLightColor }
    val isLightSystemBars = !isDark

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

@Composable
fun DarkModeScreen(
    content: @Composable () -> Unit
) {
    val previous = remember { DarkThemeController.isDark }

    DisposableEffect(Unit) {
        DarkThemeController.setDark(true)
        onDispose { DarkThemeController.setDark(previous) }
    }

    content()
}

private object DarkThemeController {
    private val _isDark = mutableStateOf(false)
    val isDark: Boolean get() = _isDark.value

    fun setDark(dark: Boolean) {
        _isDark.value = dark
    }
}

private fun Window.setSystemBarBackground() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        this.isNavigationBarContrastEnforced = false
    }

    this.statusBarColor = Color.Transparent.toArgb()
    this.navigationBarColor = Color.Transparent.toArgb()
}

object PorringTheme {
    val colors: PorringColor
        @Composable get() = LocalColor.current
    val typography: PorringTypography
        @Composable get() = LocalTypography.current
}