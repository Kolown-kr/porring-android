package com.kolown.porring.core.designsystem.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

internal val LocalTypography = staticCompositionLocalOf<PorringTypography> {
    error("LocalTypography가 제공되지 않았습니다. PorringTheme를 적용했는지 확인해주세요.")
}