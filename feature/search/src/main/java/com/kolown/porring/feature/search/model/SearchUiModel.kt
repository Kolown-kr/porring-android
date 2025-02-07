package com.kolown.porring.feature.search.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.kolown.porring.core.model.Tag
import com.kolown.porring.core.ui.base.UiState

@Immutable
internal sealed interface SearchUiState: UiState {
    val data : SearchUiModel
    fun copyData(data: SearchUiModel): SearchUiState

    @Immutable
    data object Blank: SearchUiState {
        override val data: SearchUiModel
            get() = SearchUiModel.initial()

        override fun copyData(data: SearchUiModel): SearchUiState {
            return Blank
        }
    }

    @Stable
    data class Content(
        override val data : SearchUiModel
    ): SearchUiState {
        override fun copyData(data: SearchUiModel): SearchUiState = Content(data)
    }

    @Stable
    data class Focus(
        override val data : SearchUiModel
    ): SearchUiState {
        override fun copyData(data: SearchUiModel): SearchUiState = Focus(data)
    }
}

@Stable
internal data class SearchUiModel(
    val query: String,
    val selectedTag: Tag? = null,
    val isLoading: Boolean
) {
    companion object {
        fun initial(): SearchUiModel = SearchUiModel(
            query = "",
            isLoading = false
        )
    }
}