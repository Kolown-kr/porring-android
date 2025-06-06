package com.kolown.porring.feature.search

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.data.repository.TagRepository
import com.kolown.porring.core.model.Tag
import com.kolown.porring.core.ui.base.BaseMviViewModel
import com.kolown.porring.core.ui.mapper.toUiModel
import com.kolown.porring.core.ui.model.PostUiModel
import com.kolown.porring.feature.search.model.SearchUiIntent
import com.kolown.porring.feature.search.model.SearchUiSideEffect
import com.kolown.porring.feature.search.model.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val postRepository: PostRepository,
) : BaseMviViewModel<SearchUiState, SearchUiIntent, SearchUiSideEffect>(SearchUiState.Blank) {

    private val queryFlow = MutableSharedFlow<String>()

    val tagsFlow: Flow<PagingData<Tag>> = queryFlow
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest(tagRepository::getTagBySearch)
        .cachedIn(viewModelScope)

    private val tagSelectedFlow = MutableSharedFlow<String>()

    private val clearImagesFlow: Flow<PagingData<PostUiModel>> =
        tagSelectedFlow.map { PagingData.empty() }

    private val loadImagesFlow: Flow<PagingData<PostUiModel>> = tagSelectedFlow
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest(postRepository::getPostBySearch)
        .map { pagingData -> pagingData.map { it.toUiModel() } }
        .cachedIn(viewModelScope)

    val imagesFlow: Flow<PagingData<PostUiModel>> =
        merge(clearImagesFlow, loadImagesFlow)
            .cachedIn(viewModelScope)

    override fun handleIntent(intent: SearchUiIntent) {
        when (intent) {
            is SearchUiIntent.Initial -> init()
            is SearchUiIntent.OnBackClicked -> onBackClicked()
            is SearchUiIntent.OnQueryChanged -> onQueryChanged(intent.query)
            is SearchUiIntent.OnFocusChanged -> onFocusChanged(intent.hasFocus)
            is SearchUiIntent.OnTagClicked -> onTagClicked(intent.tag)
            is SearchUiIntent.OnImageClicked -> launch {
                postSideEffect(
                    SearchUiSideEffect.NavigateToDetail(
                        tagSelectedFlow.first(),
                        intent.postId
                    )
                )
            }
        }
    }

    private fun init() = launch {
        reduce {
            copyData(
                data = data
            )
        }
    }

    private fun onBackClicked() {
        reduce {
            SearchUiState.Content(data)
        }
    }

    private fun onQueryChanged(query: String) = launch {
        if (query.isNotBlank()) queryFlow.emit(query)
        reduce {
            copyData(
                data = data.copy(
                    query = query
                )
            )
        }
    }

    private fun onFocusChanged(hasFocus: Boolean) = launch {
        reduce {
            when {
                hasFocus -> SearchUiState.Focus(data)
                !hasFocus && data.query.isBlank() -> SearchUiState.Blank
                else -> SearchUiState.Content(data)
            }
        }
    }

    private fun onTagClicked(tag: Tag) = launch {
        tagSelectedFlow.emit(tag.id)
        reduce {
            SearchUiState.Content(
                data = data.copy(
                    query = tag.name,
                    selectedTag = tag
                )
            )
        }
    }
}