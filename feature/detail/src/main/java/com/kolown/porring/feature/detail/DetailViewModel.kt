package com.kolown.porring.feature.detail

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kolown.porring.core.data.repository.AuthRepository
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
internal class DetailViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val postRepository: PostRepository,
    private val followRepository: FollowRepository,
) : BaseViewModel<DetailViewModel.State>(State()) {

    private val _posts = MutableStateFlow<PagingData<PostContentModel>>(PagingData.empty())
    val posts = _posts.cachedIn(viewModelScope)

    val isLoggedIn = authRepository.checkUserLoggedIn()

    fun init(
        type: MainMenuRoute.Detail.Type,
        order: Int,
    ) {
        postRepository.getRandomDetailPostList()
            .onEach(_posts::emit)
            .launchIn(viewModelScope)

        callbackFlow<String> { awaitClose {  } }.buffer()
    }

    fun onModeChange(isReelsMode: Boolean) = updateState {
        copy(isReelsMode = isReelsMode)
    }

    data class State(
        val isReelsMode: Boolean = true,
    )
}

data class ReactionState(
    val prev: Reactions? = null,
    val current: Reactions = Reactions.LOVE,
)