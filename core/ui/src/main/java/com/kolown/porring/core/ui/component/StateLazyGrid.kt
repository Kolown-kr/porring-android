package com.kolown.porring.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.kolown.porring.core.ui.model.PostUiModel

@Composable
fun StateLazyGrid(
    padding: PaddingValues,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    longClickEnabled: Boolean = true,
    pagingItems: LazyPagingItems<PostUiModel>,
    navigateToDetail: (PostUiModel) -> Unit = {},
    setPage: (Int) -> Unit = {},
    onLongClick: (String) -> Unit = {}
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(pagingItems.itemCount) { index ->
            val pagingItem = pagingItems[index] ?: return@items

            GalleryItem(
                postUiModel = pagingItem,
                longClickEnabled = longClickEnabled,
                onLongClickImage = { onLongClick(pagingItem.postId) },
                onClickImage = {
                    navigateToDetail(pagingItem)
                    setPage(index)
                }
            )
        }

        if (pagingItems.loadState.append !is LoadState.NotLoading) {
            item(
                key = "",
                span = StaggeredGridItemSpan.FullLine
            ) {
                PageItemFooter(
                    loadState = pagingItems.loadState.append,
                    onRetryClicked = pagingItems::retry
                )
            }
        }

        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(modifier = Modifier.height(padding.calculateBottomPadding()))
        }
    }
}

@Composable
private fun GalleryItem(
    postUiModel: PostUiModel,
    onLongClickImage: () -> Unit = {},
    onClickImage: () -> Unit = {},
    longClickEnabled: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        CoilImage(
            onClick = onClickImage,
            onLongClick = { if (longClickEnabled) onLongClickImage() },
            imageUrl = postUiModel.imageUrl,
            imageRatio = postUiModel.imageRatio,
        )
    }
}