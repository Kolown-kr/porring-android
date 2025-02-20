package com.kolown.porring.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
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
    private val _uiState = MutableStateFlow<UiState<List<PostContentModel>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _followEvent = MutableSharedFlow<PostContentModel>()
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
            postRepository.getHomeItemPosts()
                .onStart { _uiState.update { UiState.Loading } }
                .catch { e -> _uiState.update { UiState.Failure(e) } }
                .collectLatest { posts -> _uiState.update { UiState.Success(posts) } }

            postRepository.fetchHomeItemPosts()
        }
    }

    fun refreshItems() {
//        _uiState.update { UiState.Loading }
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

    fun onFollowClick(post: PostContentModel) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch
        _followEvent.emit(post)
    }

    fun cancelFollow(authorId: String) = viewModelScope.launch {
        followRepository.unFollowUser(authorId).launchIn(viewModelScope)
        _uiState.update { state ->
            state.replaceIf(
                predicate = { it.authorId == authorId },
                replacement = { it.copy(isFollower = false) }
            )
        }
    }

    fun registerFollow(authorId: String, name: String) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch
        followRepository.followUser(authorId, name).launchIn(viewModelScope)
        _uiState.update { state ->
            state.replaceIf(
                predicate = { it.authorId == authorId },
                replacement = { it.copy(isFollower = true) }
            )
        }
    }

    fun onReactionClick(postId: String, reaction: Reactions) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch

        updateReactionState(postId, reaction)

        syncReactionWithServer(postId, reaction)
    }

    private fun updateReactionState(postId: String, reaction: Reactions) {
        _uiState.update { state ->
            state.replaceIf(
                predicate = { it.postId == postId },
                replacement = { post -> post.toggleSingleReaction(reaction) }
            )
        }
    }

    private fun syncReactionWithServer(postId: String, reaction: Reactions) =
        viewModelScope.launch {
            val item = (_uiState.value as? UiState.Success<List<PostContentModel>>)?.data
                ?.find { it.postId == postId } ?: return@launch

            if (item.myReaction == reaction) {
                postRepository.removePostReaction(postId)
            } else {
                postRepository.reactPost(postId, reaction)
            }
        }

    private fun PostContentModel.toggleSingleReaction(reaction: Reactions): PostContentModel {
        val updatedReactions = reactions.toMutableList().apply {
            if (myReaction == reaction) {
                remove(reaction)
            } else {
                myReaction?.let { remove(it) }
                add(reaction)
            }
        }

        val updatedMyReaction = if (myReaction == reaction) null else reaction

        return copy(
            myReaction = updatedMyReaction,
            reactions = updatedReactions
        )
    }

    private fun <T : Any> UiState<List<T>>.replaceIf(
        predicate: (T) -> Boolean,
        replacement: (T) -> T
    ): UiState<List<T>> {
        return when (this) {
            is UiState.Success -> UiState.Success(
                data.map { item ->
                    if (predicate(item)) replacement(item) else item
                }
            )

            else -> this
        }
    }
}