package com.kolown.porring.feature.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.Reaction
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.core.ui.mapper.toUiModel
import com.kolown.porring.core.ui.model.PostUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    authRepository: AuthRepository,
    private val postRepository: PostRepository,
    private val followRepository: FollowRepository,
) : ViewModel() {
    private val _currentGalleryType = MutableStateFlow(Route.Detail.Type.DEFAULT)
    val currentGalleryType = _currentGalleryType.asStateFlow()

    private val _initialPost = MutableStateFlow<PostUiModel?>(null)
    val initialPost = _initialPost.asStateFlow()

    private val _posts = MutableStateFlow<PagingData<PostUiModel>>(PagingData.empty())
    val posts = _posts.asStateFlow().cachedIn(viewModelScope)

    private val _pageState = MutableStateFlow(PageState())
    private val pageState = _pageState.asStateFlow()

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
        val route = savedStateHandle.toRoute<Route.Detail>()

        initViewModel(
            type = route.type,
            postId = route.postId,
            authorId = route.authorId
        )

        viewModelScope.launch {
            postRepository.getPostById(route.postId ?: "")
                .onSuccess { post ->
                    _initialPost.update { post.toUiModel() }
                }
        }
        _currentGalleryType.update { route.type }
    }

    private fun initViewModel(
        type: Route.Detail.Type,
        postId: String?,
        authorId: String,
    ) = viewModelScope.launch {
        when (type) {
            Route.Detail.Type.DEFAULT -> {
                postRepository.getRandomPosts(
                    pageState = pageState,
                ).map { pagingData -> pagingData.map { it.toUiModel() } }
                    .collectLatest(_posts::emit)
            }

            Route.Detail.Type.SEARCH -> {
                postRepository.getPostBySearch(authorId)
                    .map { pagingData -> pagingData.map { it.toUiModel() } }
                    .collectLatest(_posts::emit)
            }
        }
    }

    fun updatePage(pageState: PageState) {
        _pageState.update { pageState }
    }

    private fun checkedLogIn(): Boolean {
        if (isLoggedIn.value.not()) {
            loggedInChannel.trySend(Unit)
            return false
        }
        return true
    }


    fun onReactionClick(postId: String, reaction: Reaction) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch

        postRepository.updatePostReaction(postId, reaction)
    }

    fun onFollowClick(postUiModel: PostUiModel) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch

        _followEvent.emit(postUiModel)
    }

    fun unFollowUser(authorId: String) = viewModelScope.launch {
        followRepository.unFollowUser(authorId)
            .onFailure { Log.e("UnFollowUpload", "viewModel: $it") }
    }

    fun registerFollow(authorId: String, name: String) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch

        followRepository.followUser(authorId, name)
            .onFailure {
                Log.e("Follow", "registerFollow: $it")
            }
    }
}