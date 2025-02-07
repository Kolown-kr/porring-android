package com.kolown.porring.feature.search.model

import com.kolown.porring.core.model.Tag
import com.kolown.porring.core.ui.base.UiSideEffect

internal sealed interface SearchUiSideEffect : UiSideEffect {
    data class FetchPostsByTag(val tag: Tag) : SearchUiSideEffect
    data object NavigateToDetail : SearchUiSideEffect
}