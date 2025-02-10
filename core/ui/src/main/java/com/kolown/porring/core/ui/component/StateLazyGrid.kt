package com.kolown.porring.core.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.ui.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StateLazyGrid(
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    longClickEnabled: Boolean = true,
    pagingItems: LazyPagingItems<PostContentModel>,
    navigateToDetail: () -> Unit = {},
    setPage: (Int) -> Unit = {},
    onLongClick: (String) -> Unit = {}
) {
    var showErrorScreen by remember { mutableStateOf(false) }

    LaunchedEffect(pagingItems.loadState.refresh) {
        if (pagingItems.loadState.refresh == LoadState.Loading) {
            delay(7000)
            showErrorScreen = true
        } else {
            showErrorScreen = false
        }
    }

    when {
        showErrorScreen -> {
            ErrorScreen()
        }

        pagingItems.loadState.refresh is LoadState.Error -> {
            showErrorScreen = false
            ErrorScreen()
        }

        pagingItems.loadState.refresh is LoadState.Loading -> {
            showErrorScreen = false
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    color = Primary
                )
            }
        }

        pagingItems.loadState.refresh is LoadState.NotLoading -> {
            showErrorScreen = false
            if (pagingItems.itemCount == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            modifier = Modifier.size(100.dp),
                            model = com.kolown.porring.core.designsystem.R.drawable.ic_question_mark,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = stringResource(R.string.string_no_post),
                            color = Primary
                        )
                    }
                }
            } else {
                CompositionLocalProvider(
                    LocalOverscrollConfiguration provides null
                ) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalItemSpacing = 8.dp,
                        content = {
                            items(pagingItems.itemCount) { index ->
                                pagingItems[index]?.let {
                                    GalleryItem(
                                        postContentModel = it,
                                        longClickEnabled = longClickEnabled,
                                        onLongClickImage = { onLongClick(it.postId) },
                                        onClickImage = {
                                            navigateToDetail()
                                            setPage(index)
                                        },
                                    )
                                }
                            }

                            if (pagingItems.loadState.append !is LoadState.NotLoading) {
                                item(key = "", span = StaggeredGridItemSpan.FullLine) {
                                    PageItemFooter(loadState = pagingItems.loadState.append) {
                                        pagingItems.retry()
                                    }
                                }
                            }
                        }
                    )
                }
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
    val isDialogVisible = rememberSaveable { mutableStateOf(false) }
    val imageRatio = rememberSaveable { mutableFloatStateOf(4f / 5f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        CoilImage(
            onClick = onClickImage,
            onLongClick = {
                if (longClickEnabled) isDialogVisible.value = true
            },
            imageUrl = postContentModel.imageUrl,
            imageRatio = imageRatio.floatValue,
            updateImageRatio = { imageRatio.floatValue = it }
        )
    }

    if (isDialogVisible.value) {
        UnfollowCheckDialog(
            title = stringResource(R.string.string_post_delete_title),
            description = stringResource(R.string.string_question_delete),
            dismissText = stringResource(R.string.string_cancel),
            confirmText = stringResource(R.string.string_remove),
            onDismissRequest = { isDialogVisible.value = false },
            onConfirm = {
                onLongClickImage()
                isDialogVisible.value = false
            }
        )
    }
}

