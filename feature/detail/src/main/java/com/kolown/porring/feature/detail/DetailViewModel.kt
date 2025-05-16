package com.kolown.porring.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.data.repository.PostType
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.navigation.MainMenuRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
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

    private val _posts = MutableStateFlow<PagingData<PostContentModel>>(PagingData.empty())
    val posts = _posts.asStateFlow().cachedIn(viewModelScope)

    private val _pageState = MutableStateFlow(PageState())
    private val pageState = _pageState.asStateFlow()

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
            val type = savedStateHandle.get<MainMenuRoute.Detail.Type>("type") ?: return@launch
            val postId = savedStateHandle.get<String>("postId") ?: ""
            val authorId = savedStateHandle.get<String>("authorId") ?: ""

            when (type) {
                MainMenuRoute.Detail.Type.DEFAULT -> {
                    postRepository.getPagingItemPosts(
                        postType = PostType.RANDOM_DETAIL,
                        pageState = pageState,
                    )
                        .collectLatest(_posts::emit)
                }

                MainMenuRoute.Detail.Type.FOLLOW -> {
                    postRepository.getPagingItemPosts(
                        postType = PostType.USER_DETAIL,
                        pageState = pageState,
                        authorId = authorId,
                        postId = postId
                    )
                        .collectLatest(_posts::emit)
                }


                MainMenuRoute.Detail.Type.MY -> {
                    postRepository.getPagingItemPosts(
                        postType = PostType.USER_DETAIL,
                        pageState = pageState,
                        authorId = authorId,
                        postId = postId
                    )
                        .collectLatest(_posts::emit)
                }
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

    fun onReactionClick(postId: String, reaction: Reactions) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch
        postRepository.reactPost(postId, reaction)
    }

    fun onFollowClick(post: PostContentModel) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch

        _followEvent.emit(post)
    }

    fun cancelFollow(authorId: String) = viewModelScope.launch {
        followRepository.unFollowUser(authorId).launchIn(viewModelScope)
    }

    fun registerFollow(authorId: String, name: String) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch

        followRepository.followUser(authorId, name).launchIn(viewModelScope)
    }
}