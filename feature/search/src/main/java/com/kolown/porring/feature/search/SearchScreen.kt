package com.kolown.porring.feature.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.ui.theme.Gray
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.designsystem.ui.theme.Surface2
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Tag
import com.kolown.porring.feature.search.component.PostItem
import com.kolown.porring.feature.search.component.TagSearchBar
import com.kolown.porring.feature.search.model.SearchUiIntent
import com.kolown.porring.feature.search.model.SearchUiModel
import com.kolown.porring.feature.search.model.SearchUiState

@Composable
internal fun SearchRoute(
    padding: PaddingValues,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val tags = viewModel.tagsFlow.collectAsLazyPagingItems()
    val images = viewModel.imagesFlow.collectAsLazyPagingItems()

    SearchScreen(
        padding = padding,
        state = state,
        tags = tags,
        images = images,
        onBackClick = { viewModel.handleIntent(SearchUiIntent.OnBackClicked) },
        onSearchQueryChanged = { viewModel.handleIntent(SearchUiIntent.OnQueryChanged(it)) },
        onFocusChanged = { viewModel.handleIntent(SearchUiIntent.OnFocusChanged(it)) },
        onTagClicked = { viewModel.handleIntent(SearchUiIntent.OnTagClicked(it)) }
    )
}

@Composable
private fun SearchScreen(
    padding: PaddingValues,
    state: SearchUiState = SearchUiState.Content(SearchUiModel.initial()),
    tags: LazyPagingItems<Tag>? = null,
    images: LazyPagingItems<PostContentModel>? = null,
    onBackClick: () -> Unit = {},
    onSearchQueryChanged: (String) -> Unit = {},
    onFocusChanged: (Boolean) -> Unit = {},
    onTagClicked: (Tag) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
    ) {
        TagSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .padding(
                    top = 16.dp,
                    bottom = 8.dp,
                )
                .height(54.dp),
            text = state.data.query,
            isFocus = state is SearchUiState.Focus,
            onValueChange = onSearchQueryChanged,
            onFocusChanged = onFocusChanged,
            onClearClick = { onSearchQueryChanged("") }
        )

        when (state) {
            is SearchUiState.Blank -> {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        stringResource(R.string.string_input_keyword),
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.Center),
                        color = Gray
                    )
                }
            }

            is SearchUiState.Focus -> {
                tags?.let {
                    SearchTags(
                        tags = it,
                        onTagClicked = onTagClicked
                    )
                }

                BackHandler(onBack = onBackClick)
            }

            is SearchUiState.Content -> {
                state.data.selectedTag?.let { tag ->
                    Text(
                        text = "# ${tag.name}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                images?.let {
                    SearchImages(
                        images = it,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchTags(
    tags: LazyPagingItems<Tag>,
    onTagClicked: (Tag) -> Unit = {}
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = tags.itemSnapshotList,
            key = { it?.id.orEmpty() }
        ) { tag ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        tag?.let { onTagClicked(it) }
                    }
                    .padding(
                        vertical = 14.dp,
                        horizontal = 16.dp
                    ),
                text = tag?.let { "# ${it.name}" }.orEmpty(),
                fontSize = 14.sp,
            )
        }

        item {
            VerticalDivider(
                thickness = 8.dp,
                color = Surface2
            )
        }
    }
}

@Composable
private fun SearchImages(images: LazyPagingItems<PostContentModel>) {
    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 16.dp),
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = images.itemSnapshotList,
            key = { it?.postId.orEmpty() }
        ) { image ->
            if (image != null) {
                PostItem(
                    post = image,
                    onClick = {}
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .aspectRatio(1f)
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    PorringTheme(true) {
        SearchScreen(
            padding = PaddingValues(),
            state = SearchUiState.Content(SearchUiModel.initial())
        )
    }
}