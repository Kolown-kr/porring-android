package com.kolown.porring.core.designsystem.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object PorringTypography {
    val headline = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    val title = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold
    )
    val body = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    )
    val caption = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal
    )
    val label = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
    val subLabel = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium
    )
}
