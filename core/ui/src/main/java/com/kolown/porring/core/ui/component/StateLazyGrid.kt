package com.kolown.porring.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.kolown.porring.core.model.PostContentModel

@Composable
fun StateLazyGrid(
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    longClickEnabled: Boolean = true,
    pagingItems: LazyPagingItems<PostContentModel>,
    navigateToDetail: () -> Unit = {},
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
            val pagingItem = pagingItems[index]

            if (pagingItem != null) {
                GalleryItem(
                    postContentModel = pagingItem,
                    longClickEnabled = longClickEnabled,
                    onLongClickImage = { onLongClick(pagingItem.postId) },
                    onClickImage = {
                        navigateToDetail()
                        setPage(index)
                    }
                )
            }
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
    }
}

@Composable
private fun GalleryItem(
    postContentModel: PostContentModel,
    onLongClickImage: () -> Unit = {},
    onClickImage: () -> Unit = {},
    longClickEnabled: Boolean = true
) {
    val imageRatio = rememberSaveable { mutableFloatStateOf(4f / 5f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        CoilImage(
            onClick = onClickImage,
            onLongClick = { if (longClickEnabled) onLongClickImage() },
            imageUrl = postContentModel.imageUrl,
            imageRatio = imageRatio.floatValue,
            updateImageRatio = { imageRatio.floatValue = it }
        )
    }
}