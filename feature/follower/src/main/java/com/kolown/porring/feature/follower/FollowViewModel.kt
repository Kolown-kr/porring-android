package com.kolown.porring.feature.follower

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.model.FollowerThumbnail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FollowerViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val followerRepository: FollowRepository
) : ViewModel() {
    private val _followerItems = MutableStateFlow<PagingData<FollowerThumbnail>>(PagingData.empty())
    val followerItems = _followerItems.cachedIn(viewModelScope)

    val isLoggedIn = authRepository.checkUserLoggedIn()

    fun init() {
        followerRepository.getFollowerDataSourcePagingFlow()
            .onEach(_followerItems::emit)
            .launchIn(viewModelScope)
    }
}