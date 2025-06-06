package com.kolown.porring.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.kolown.porring.core.data.repository.PostRepository
import com.kolown.porring.core.ui.mapper.toUiModel
import com.kolown.porring.core.ui.model.PostUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class SearchImageViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {
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

    fun selectTag(tagId: String) {
        viewModelScope.launch {
            tagSelectedFlow.emit(tagId)
        }
    }
}