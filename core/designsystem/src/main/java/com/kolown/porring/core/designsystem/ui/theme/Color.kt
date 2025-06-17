package com.kolown.porring.core.designsystem.ui.theme

import androidx.compose.ui.graphics.Color

val Background = Color(0xFFFFFFFF)
val BackgroundDark = Color(0xFF020316)
val Primary = Color(0xFF598AFF)
val PrimaryDark = Color(0xFF00BBFF)
val PrimaryUnActive = Color(0xFFB9DFFA)
val PrimaryContainerDark = Color(0xFF151D37)
val OutlineVariant = Color(0xFFE2CBFF)
val Error = Color(0xFFFF568A)
val SurfaceError = Color(0xFFFFE3EB)
val Surface = Color(0xFFF8F9FF)
val Surface2 = Color(0xFFEAEEFF)
val Gray = Color(0xFF8D8D8D)
val PrimaryUnActiveDark = Color(0xFF8D8D8D)
val SnackBarContainer = Color(0xFF363535)
val ShimmerLightGray = Color(0xFFBFBFBF) // 중간 연한 그레이
val ShimmerDarkGray = Color(0xFFADADAD) // 중간 진한 그레이

interface PorringColor {
    val primary: Color
    val onPrimary: Color
    val primaryContainer: Color
    val onPrimaryContainer: Color
    val secondary: Color
    val tertiary: Color
    val onSecondary: Color
    val onTertiary: Color
    val secondaryContainer: Color
    val tertiaryContainer: Color
    val onSecondaryContainer: Color
    val onTertiaryContainer: Color
    val background: Color
    val outline: Color
    val error: Color
    val onBackground: Color
    val outlineVariant: Color
    val onError: Color
    val surface: Color
    val shadow: Color
    val errorContainer: Color
    val onSurface: Color
    val onErrorContainer: Color
}

internal object PorringLightColor : PorringColor {
    override val primary = Color(0xFF598AFF)
    override val onPrimary = Color(0xFFFFFFFF)
    override val primaryContainer = Color(0xFFC9DBFF)
    override val onPrimaryContainer = Color(0xFF102A54)

    override val secondary = Color(0xFF444655)
    override val onSecondary = Color(0xFFFFFFFF)
    override val secondaryContainer = Color(0xFFD4D6E8)
    override val onSecondaryContainer = Color(0xFF272937)

    override val tertiary = Color(0xFFD0D7FF)
    override val onTertiary = Color(0xFFF7F8FF)
    override val tertiaryContainer = Color(0xFFCDD5FF)
    override val onTertiaryContainer = Color(0xFF112650)

    override val background = Color(0xFFF7F8FF)
    override val onBackground = Color(0xFF1E2230)
    override val surface = Color(0xFFFFFFFF)
    override val onSurface = Color(0xFF1E2230)

    override val outline = Color(0xFFC7CDD8)
    override val outlineVariant = Color(0xFFE2E6ED)
    override val shadow = Color(0xFFCCCCCC)

    override val error = Color(0xFFF75670)
    override val onError = Color(0xFFFFFFFF)
    override val errorContainer = Color(0xFFFFC9DA)
    override val onErrorContainer = Color(0xFF89001F)
}

internal object PorringDarkColor : PorringColor {
    override val primary = Color(0xFF7DAEFF)
    override val onPrimary = Color(0xFF002449)
    override val primaryContainer = Color(0xFF598AFF)
    override val onPrimaryContainer = Color(0xFFE2ECFF)

    override val secondary = Color(0xFFC2C6D4)
    override val onSecondary = Color(0xFF2D2F36)
    override val secondaryContainer = Color(0xFF50525B)
    override val onSecondaryContainer = Color(0xFFE7E9F4)

    override val tertiary = Color(0xFF5F678D)
    override val onTertiary = Color(0xFF2B3152)
    override val tertiaryContainer = Color(0xFF535A7A)
    override val onTertiaryContainer = Color(0xFFE8EAFF)

    override val background = Color(0xFF0D0E11)
    override val onBackground = Color(0xFFB4B6BA)
    override val surface = Color(0xFF1A1B20)
    override val onSurface = Color(0xFFE5E7EC)

    override val outline = Color(0xFF8A8D99)
    override val outlineVariant = Color(0xFF3C3E44)
    override val shadow = Color(0xFF0C0C0D)

    override val error = Color(0xFFFFB4AB)
    override val onError = Color(0xFF690005)
    override val errorContainer = Color(0xFF93000A)
    override val onErrorContainer = Color(0xFFFFDAD6)
}