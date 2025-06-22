package com.kolown.porring.core.ui.compositionlocal

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.ui.model.SnackBarBridge


val LocalSnackBarBridge =
    compositionLocalOf<SnackBarBridge> { error("No SnackBarHostState provided") }

suspend fun SnackbarHostState.showSnackBarWithData(data: SnackBarEvent) =
    showSnackbar(
        message = data.message,
        actionLabel = data.actionLabel,
        duration = SnackbarDuration.Short
    )

