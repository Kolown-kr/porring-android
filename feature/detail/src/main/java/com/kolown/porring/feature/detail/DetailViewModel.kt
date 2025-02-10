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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    val isLoggedIn = authRepository.checkUserLoggedIn()

    fun init(
        type: MainMenuRoute.Detail.Type,
        order: Int,
    ) {
        postRepository.getRandomDetailPostList()
            .onEach(_posts::emit)
            .launchIn(viewModelScope)
    }

    fun onReactionClick(postId: String, reaction: Reactions) = viewModelScope.launch {
        postRepository.reactPost(postId, reaction)
        _posts.update {
            _posts.value.replaceIf(
                predicate = { it.postId == postId },
                replacement = { it.copy(myReaction = reaction) }
            )
        }
    }

    fun onFollowClick(post: PostContentModel) = viewModelScope.launch {
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