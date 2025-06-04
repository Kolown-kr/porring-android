package com.kolown.porring.feature.detail_my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kolown.porring.core.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailMyViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {
    val pagingItems = postRepository.getMyPosts().cachedIn(viewModelScope)
}