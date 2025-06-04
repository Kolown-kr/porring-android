package com.kolown.porring.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.data.repository.PostType
import com.kolown.porring.core.data.repository.UserRepository
import com.kolown.porring.core.model.PostContentModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    authRepository: AuthRepository
) : ViewModel() {
    private val _posts = MutableStateFlow<PagingData<PostContentModel>>(PagingData.empty())
    val posts = _posts.asStateFlow()

    private var _firstPage = 0
    val firstPage get() = _firstPage

    private val currentUserId = MutableStateFlow("")

    private val _isDeleteSuccess = MutableStateFlow(false)
    val isDeleteSuccess = _isDeleteSuccess.asStateFlow()

    val loginState = authRepository.checkUserLoggedIn()

    init {
        viewModelScope.launch {
            setUserId()
            postRepository.clearPagingItems()
            postRepository.getPagingItemPosts(
                postType = PostType.USER_GALLERY,
                authorId = currentUserId.value,
                pageState = null
            ).collectLatest(_posts::emit)
        }
    }

    fun setPage(page: Int) {
        _firstPage = page
    }

    fun onClickItem(post: PostContentModel) {
        viewModelScope.launch {
//            postRepository.clearPagingItems()
//            postRepository.insertPagingItem(post)
        }
    }

    fun setUserId() {
        val newId = userRepository.getUserData().getOrThrow()
        if (currentUserId.value != newId) {
            currentUserId.update { newId }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            postRepository.deletePost(postId)
                .collect { result -> _isDeleteSuccess.update { result.isSuccess } }
        }
    }
}
