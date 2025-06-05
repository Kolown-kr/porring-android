package com.kolown.porring.feature.detail_my

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailMyUiState(
    val initialPage: Int = 0,
    val isFocusMode: Boolean = false,
    val isLoggedIn: Boolean = false,
    val focusImageUrl: String = "",
    val focusImageRatio: Float = 4f / 5f,
)

@HiltViewModel
class DetailMyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val pagingItems = postRepository.getMyPosts().cachedIn(viewModelScope)

    private var _uiState = MutableStateFlow(DetailMyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val initialPage = savedStateHandle.get<Int>("pageIndex") ?: 0

        _uiState.update { state -> state.copy(initialPage = initialPage) }

        viewModelScope.launch {
            authRepository.checkUserLoggedIn().collectLatest {
                _uiState.update { state ->
                    state.copy(isLoggedIn = it)
                }
            }
        }
    }

    fun onAction(intent: DetailMyIntent) {
        when (intent) {
            is DetailMyIntent.ChangeToFocusMode -> {
                _uiState.update { state -> reduce(state, intent) }
            }

            is DetailMyIntent.ChangeToDefaultMode -> {
                _uiState.update { state -> reduce(state, intent) }
            }
        }
    }

    private fun reduce(state: DetailMyUiState, intent: DetailMyIntent): DetailMyUiState {
        return when (intent) {
            is DetailMyIntent.ChangeToFocusMode -> {
                state.copy(
                    isFocusMode = true,
                    focusImageUrl = intent.url,
                    focusImageRatio = intent.ratio
                )
            }

            is DetailMyIntent.ChangeToDefaultMode -> {
                state.copy(isFocusMode = false, focusImageUrl = "")
            }
        }
    }
}

sealed interface DetailMyIntent {
    data class ChangeToFocusMode(val url: String, val ratio: Float) : DetailMyIntent
    data object ChangeToDefaultMode : DetailMyIntent
}