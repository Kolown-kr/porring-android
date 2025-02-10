package com.kolown.porring.feature.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.navigation.MainMenuRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DetailViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val postRepository: PostRepository,
    private val followRepository: FollowRepository,
) : ViewModel() {

    private val _posts = MutableStateFlow<PagingData<PostContentModel>>(PagingData.empty())
    val posts = _posts.cachedIn(viewModelScope)

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

    fun init(
        type: MainMenuRoute.Detail.Type,
        order: Int,
    ) {
        postRepository.getRandomDetailPostList()
            .onEach(_posts::emit)
            .launchIn(viewModelScope)
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
        _posts.update {
            _posts.value.replaceIf(
                predicate = { it.postId == postId },
                replacement = { it.copy(myReaction = reaction) }
            )
        }
    }

    fun onFollowClick(post: PostContentModel) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch
        if (post.isFollower) {
            followRepository.unFollowUser(post.authorId)
            _posts.update {
                _posts.value.replaceIf(
                    predicate = { it.authorId == post.authorId },
                    replacement = { it.copy(isFollower = false) }
                )
            }
        } else {
            _followEvent.emit(post)
        }
    }

    fun registerFollow(authorId: String, name: String) = viewModelScope.launch {
        if (checkedLogIn().not()) return@launch
        followRepository.followUser(authorId, name)
        _posts.update {
            _posts.value.replaceIf(
                predicate = { it.authorId == authorId },
                replacement = { it.copy(isFollower = true) }
            )
        }
    }

    private inline fun <T : Any> PagingData<T>.replaceIf(
        crossinline predicate: (T) -> Boolean,
        crossinline replacement: (T) -> T,
    ): PagingData<T> = map { if (predicate(it)) replacement(it) else it }
}