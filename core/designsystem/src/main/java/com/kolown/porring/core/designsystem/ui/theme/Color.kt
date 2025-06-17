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
    val Primary: Color
    val OnPrimary: Color
    val PrimaryContainer: Color
    val OnPrimaryContainer: Color
    val Secondary: Color
    val Tertiary: Color
    val OnSecondary: Color
    val OnTertiary: Color
    val SecondaryContainer: Color
    val TertiaryContainer: Color
    val OnSecondaryContainer: Color
    val OnTertiaryContainer: Color
    val Background: Color
    val Outline: Color
    val Error: Color
    val OnBackground: Color
    val OutlineVariant: Color
    val OnError: Color
    val Surface: Color
    val Shadow: Color
    val ErrorContainer: Color
    val OnSurface: Color
    val OnErrorContainer: Color
}

internal object PorringLightColor : PorringColor {
    override val Primary = Color(0xFF598AFF)
    override val OnPrimary = Color(0xFFFFFFFF)
    override val PrimaryContainer = Color(0xFFC9DBFF)
    override val OnPrimaryContainer = Color(0xFF102A54)

    override val Secondary = Color(0xFF444655)
    override val OnSecondary = Color(0xFFFFFFFF)
    override val SecondaryContainer = Color(0xFFD4D6E8)
    override val OnSecondaryContainer = Color(0xFF272937)

    override val Tertiary = Color(0xFFD0D7FF)
    override val OnTertiary = Color(0xFFF7F8FF)
    override val TertiaryContainer = Color(0xFFCDD5FF)
    override val OnTertiaryContainer = Color(0xFF112650)

    override val Background = Color(0xFFF7F8FF)
    override val OnBackground = Color(0xFF1E2230)
    override val Surface = Color(0xFFFFFFFF)
    override val OnSurface = Color(0xFF1E2230)

    override val Outline = Color(0xFFC7CDD8)
    override val OutlineVariant = Color(0xFFE2E6ED)
    override val Shadow = Color(0xFFCCCCCC)

    override val Error = Color(0xFFF75670)
    override val OnError = Color(0xFFFFFFFF)
    override val ErrorContainer = Color(0xFFFFC9DA)
    override val OnErrorContainer = Color(0xFF89001F)
}

internal object PorringDarkColor : PorringColor {
    override val Primary = Color(0xFF7DAEFF)
    override val OnPrimary = Color(0xFF002449)
    override val PrimaryContainer = Color(0xFF598AFF)
    override val OnPrimaryContainer = Color(0xFFE2ECFF)

    override val Secondary = Color(0xFFC2C6D4)
    override val OnSecondary = Color(0xFF2D2F36)
    override val SecondaryContainer = Color(0xFF50525B)
    override val OnSecondaryContainer = Color(0xFFE7E9F4)

    override val Tertiary = Color(0xFF5F678D)
    override val OnTertiary = Color(0xFF2B3152)
    override val TertiaryContainer = Color(0xFF535A7A)
    override val OnTertiaryContainer = Color(0xFFE8EAFF)

    override val Background = Color(0xFF0D0E11)
    override val OnBackground = Color(0xFFB4B6BA)
    override val Surface = Color(0xFF1A1B20)
    override val OnSurface = Color(0xFFE5E7EC)

    override val Outline = Color(0xFF8A8D99)
    override val OutlineVariant = Color(0xFF3C3E44)
    override val Shadow = Color(0xFF0C0C0D)

    override val Error = Color(0xFFFFB4AB)
    override val OnError = Color(0xFF690005)
    override val ErrorContainer = Color(0xFF93000A)
    override val OnErrorContainer = Color(0xFFFFDAD6)
}