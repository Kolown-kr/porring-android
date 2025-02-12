package com.kolown.porring.feature.search.model

import com.kolown.porring.core.ui.base.UiSideEffect

internal sealed interface SearchUiSideEffect : UiSideEffect {
    data object NavigateToDetail : SearchUiSideEffect
}