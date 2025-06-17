package com.kolown.porring.core.designsystem.ui.theme

import android.app.Activity
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    background = Background,
    surface = Surface,

    /* Other default colors to override
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun PorringTheme(
    isLightBars: Boolean,
    isDark: Boolean = false,
    content: @Composable () -> Unit,
) {
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
