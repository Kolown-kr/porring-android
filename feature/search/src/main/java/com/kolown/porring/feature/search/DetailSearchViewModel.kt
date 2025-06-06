package com.kolown.porring.feature.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kolown.porring.core.data.repository.FollowRepository
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.data.repository.UserRepository
import com.kolown.porring.core.model.Reaction
import com.kolown.porring.core.ui.model.PostUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailSearchViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    var firstPage = 0
        private set

    private val reactionStateFlow = MutableStateFlow<Map<String, ReactionState>>(emptyMap())

    private val _followSharedFlow = MutableSharedFlow<Pair<String, Boolean>>(0)
    val followState = _followSharedFlow.asSharedFlow()

    fun setPage(page: Int) {
        firstPage = page
    }

    fun selectReaction(imageItem: PostUiModel, reaction: Reaction) {
        val currentReaction = imageItem.myReaction

        viewModelScope.launch {
            postRepository.updatePostReaction(imageItem.postId, reaction)
        }
        updateReactionState(imageItem.postId, currentReaction, reaction)
    }

    private fun updateReactionState(
        postId: String,
        prevReaction: Reaction?,
        currentReaction: Reaction,
    ) {
        reactionStateFlow.update { reactionState ->
            val newState = reactionState.toMutableMap()
            newState[postId] = ReactionState(prev = prevReaction, current = currentReaction)
            newState
        }
    }

    fun followUser(id: String, name: String) {
        viewModelScope.launch {
            followRepository.followUser(id, name)
                .catch { Log.e("FollowUpload", "viewModel: $it") }
                .launchIn(viewModelScope)
            _followSharedFlow.emit(Pair(id, true))
        }
    }

    fun unFollowUser(id: String) {
        viewModelScope.launch {
            followRepository.unFollowUser(id)
                .catch { Log.e("UnFollowUpload", "viewModel: $it") }
                .launchIn(viewModelScope)
            _followSharedFlow.emit(Pair(id, false))
        }
    }

    fun checkPostIsMine(authorId: String) = userRepository.checkUserId(authorId)


    companion object {
        const val SEARCH_DEBOUNCE_TIME_MILLIS = 300L
    }
}

data class ReactionState(
    val prev: Reaction? = null,
    val current: Reaction = Reaction.LOVE,
)