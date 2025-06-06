package com.kolown.porring.feature.follower

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.model.FollowWithThumbnail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowerViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val followerRepository: FollowRepository
) : ViewModel() {
    private val _followerItems =
        MutableStateFlow<PagingData<FollowWithThumbnail>>(PagingData.empty())
    val followerItems = _followerItems.cachedIn(viewModelScope)

    val isLoggedIn = authRepository.checkUserLoggedIn()

    init {
        viewModelScope.launch {
            followerRepository.getFollowsWithPaging()
                .collectLatest(_followerItems::emit)
        }
    }
}