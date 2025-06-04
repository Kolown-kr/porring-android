package com.kolown.porring.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.data.repository.UserRepository
import com.kolown.porring.core.model.MyPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val _posts = MutableStateFlow<PagingData<MyPost>>(PagingData.empty())
    val posts: StateFlow<PagingData<MyPost>> = _posts.asStateFlow()

    private val currentUserId = MutableStateFlow("")

    private val _isDeleteSuccess = MutableStateFlow(false)
    val isDeleteSuccess = _isDeleteSuccess.asStateFlow()

    val loginState = authRepository.checkUserLoggedIn()

    init {
        viewModelScope.launch {
            setUserId()
            postRepository.getMyPosts()
                .cachedIn(viewModelScope)
                .collectLatest(_posts::emit)
            postRepository.fetchMyPosts()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            postRepository.fetchMyPosts()
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
