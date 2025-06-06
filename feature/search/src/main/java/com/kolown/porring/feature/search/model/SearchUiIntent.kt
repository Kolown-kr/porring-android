package com.kolown.porring.feature.search.model

import com.kolown.porring.core.model.Tag
import com.kolown.porring.core.ui.base.UiIntent

sealed interface SearchUiIntent : UiIntent {
    data object Initial : SearchUiIntent
    data object OnBackClicked : SearchUiIntent
    data class OnQueryChanged(val query: String) : SearchUiIntent
    data class OnFocusChanged(val hasFocus: Boolean) : SearchUiIntent
    data class OnTagClicked(val tag: Tag) : SearchUiIntent
    data class OnImageClicked(val postId: String) : SearchUiIntent
}