package com.kolown.porring.feature.their

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.data.repository.PostType
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.PostUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TheirUiState(
    val title: String = "",
)

@HiltViewModel
class TheirViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val followRepository: FollowRepository,
) : ViewModel() {
    private val _pagingItems = MutableStateFlow<PagingData<PostUiModel>>(PagingData.empty())
    val pagingItems = _pagingItems.asStateFlow().cachedIn(viewModelScope)

    private val _uiState = MutableStateFlow(TheirUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val authorId = savedStateHandle.get<String>("authorId") ?: ""

        setFollowerName(authorId)
        getPosts(authorId)
    }

    private fun getPosts(authorId: String) {
        viewModelScope.launch {
            postRepository.clearPagingItems()
            postRepository.getPagingItemPosts(
                postType = PostType.USER_GALLERY,
                postId = null,
                authorId = authorId,
                pageState = MutableStateFlow(PageState())
            ).collectLatest(_pagingItems::emit)
        }
    }

    private fun setFollowerName(followerId: String) {
        viewModelScope.launch {
            followRepository.getFollowerName(followerId)
                .collect { name ->
                    val followerName = name ?: "Annonymous"

                    _uiState.update { uiState ->
                        uiState.copy(title = "${followerName}'s Gallery")
                    }
                }
        }
    }
}