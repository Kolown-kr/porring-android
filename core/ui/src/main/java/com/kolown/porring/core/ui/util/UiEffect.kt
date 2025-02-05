package com.kolown.porring.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.kolown.porring.core.ui.base.BaseMviViewModel
import com.kolown.porring.core.ui.base.UiSideEffect

@Composable
fun <E: UiSideEffect>LaunchSideEffect(
    viewModel: BaseMviViewModel<*, *, E>,
    key: Any? = Unit,
    onEffect: (E) -> Unit
) {
    LaunchedEffect(key) {
        viewModel.sideEffect.collect {
            onEffect(it)
        }
    }
}