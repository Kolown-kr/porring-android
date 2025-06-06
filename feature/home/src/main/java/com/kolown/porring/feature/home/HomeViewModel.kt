package com.kolown.porring.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.model.PostUiModel
import com.kolown.porring.core.model.Reaction
import com.kolown.porring.core.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val followRepository: FollowRepository,
    authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<PostUiModel>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _followEvent = MutableSharedFlow<PostUiModel>()
    val followEvent = _followEvent.asSharedFlow()

    private val loggedInChannel = Channel<Unit>(Channel.UNLIMITED)
    val loggedInEvent = loggedInChannel.receiveAsFlow()

    private val isLoggedIn = authRepository.checkUserLoggedIn()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    init {
        viewModelScope.launch {
            postRepository.fetchHomeItemPosts()
            postRepository.getHomeItemPosts()
                .onStart { _uiState.update { UiState.Loading } }
                .catch { e -> _uiState.update { UiState.Failure(e) } }
                .collectLatest { posts -> _uiState.update { UiState.Success(posts) } }
        }
    }

    fun refreshItems() {
        _uiState.update { UiState.Loading }
        viewModelScope.launch {
            postRepository.fetchHomeItemPosts()
        }
    }

    private fun checkedLogIn(): Boolean {
        if (isLoggedIn.value.not()) {
            loggedInChannel.trySend(Unit)
            return false
        }
        return true
    }

    fun onFollowClick(postUiModel: PostUiModel) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch

        _followEvent.emit(postUiModel)
    }

    fun cancelFollow(authorId: String) = viewModelScope.launch {
        followRepository.unFollowUser(authorId).launchIn(viewModelScope)
    }

    fun registerFollow(authorId: String, name: String) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch

        followRepository.followUser(authorId, name).launchIn(viewModelScope)
    }

    fun onReactionClick(postId: String, reaction: Reaction) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch

        reactPost(postId, reaction)
    }

    private fun reactPost(postId: String, reaction: Reaction) =
        viewModelScope.launch {
            postRepository.updatePostReaction(postId, reaction)
        }

    fun onClickItem(postUiModel: PostUiModel) {
        viewModelScope.launch {
            postRepository.clearPagingItems()
            postRepository.insertPagingItem(postUiModel)
        }
    }
}