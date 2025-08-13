package com.kolown.porring.core.designsystem.ui.theme

import android.app.Activity
import android.os.Build
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
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
    DisposableEffect(Unit) {
        DarkThemeController.push()
        onDispose { DarkThemeController.pop() }
    }

    content()
}

private object DarkThemeController {
    private val _depth = mutableIntStateOf(0)
    val isDark: Boolean get() = _depth.intValue > 0

    fun push() { _depth.intValue++ }
    fun pop() { _depth.intValue = (_depth.intValue - 1).coerceAtLeast(0) }
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