package com.kolown.porring.feature.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
    private val postRepository: PostRepository,
    authRepository: AuthRepository
) : ViewModel() {
    val pagingItems = postRepository.getMyPosts().cachedIn(viewModelScope)
    val loginState = authRepository.checkUserLoggedIn()

    fun refresh() {
        viewModelScope.launch {
            postRepository.fetchMyPosts()
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            if (postRepository.deletePost(postId).isSuccess) {
                postRepository.fetchMyPosts()
            }
        }
    }
}
