package com.kolown.porring.core.ui.compositionlocal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.compositionLocalOf

val LocalPaddingValues = compositionLocalOf<PaddingValues> {
    error("No LocalPaddingValues provided")
}