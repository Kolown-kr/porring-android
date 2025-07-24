package com.kolown.porring.feature.detail_my

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.component.button.PorringIconButton
import com.kolown.porring.core.model.MyPost
import com.kolown.porring.core.ui.component.CoilImage
import com.kolown.porring.core.ui.component.FullscreenImageViewer

@Composable
internal fun DetailMyRoute(
    popBackStack: () -> Unit,
    viewModel: DetailMyViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.pagingItems.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(
        initialPage = uiState.initialPage,
        pageCount = { pagingItems.itemCount }
    )

    if (uiState.isFocusMode) {
        FullscreenImageViewer(
            imageUrl = uiState.focusImageUrl,
            imageRatio = uiState.focusImageRatio,
            onDismiss = { viewModel.onAction(DetailMyIntent.ChangeToDefaultMode) }
        )
    } else {
        DetailMyScreen(
            pagerState = pagerState,
            popBackStack = popBackStack,
            pagingItems = pagingItems,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
private fun DetailMyScreen(
    pagerState: PagerState = rememberPagerState { 0 },
    pagingItems: LazyPagingItems<MyPost>,
    popBackStack: () -> Unit = {},
    onAction: (DetailMyIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        PorringTopAppBar(
            navigationIcon = {
                PorringIconButton(
                    icon = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    onClick = popBackStack,
                    contentDescription = null,
                    color = Color.White
                )
            },
        )

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            key = { index ->
                val item = pagingItems[index]
                "${item?.postId}"
            },
            beyondViewportPageCount = 3,
        ) { page: Int ->
            val post = pagingItems[page] ?: return@HorizontalPager

            DetailContent(
                post = post,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun DetailContent(
    post: MyPost,
    onAction: (DetailMyIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 5f),
            contentAlignment = Alignment.Center
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxWidth(),
                imageUrl = post.imageUrl,
                imageRatio = post.imageRatio,
                onClick = {
                    onAction(
                        DetailMyIntent.ChangeToFocusMode(
                            url = post.imageUrl,
                            ratio = post.imageRatio
                        )
                    )
                },
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )

                if (post.tags.isNotEmpty()) {
                    Text(
                        text = buildString {
                            post.tags.forEachIndexed { index, tag ->
                                if (index != 0) append(", ")
                                append("# $tag ")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))
    }
}