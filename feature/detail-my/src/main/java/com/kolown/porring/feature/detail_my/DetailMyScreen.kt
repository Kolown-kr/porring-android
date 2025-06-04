package com.kolown.porring.feature.detail_my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.BackgroundDark
import com.kolown.porring.core.model.MyPost
import com.kolown.porring.core.ui.component.CoilImage

@Composable
internal fun DetailMyRoute(
    padding: PaddingValues,
    postId: String,
    popBackStack: () -> Unit,
    viewModel: DetailMyViewModel = hiltViewModel()
) {
    val pagingItems = viewModel.pagingItems.collectAsLazyPagingItems()
    val imageRatioMap = remember { mutableStateMapOf<Int, Float>() }

    DetailMyScreen(
        padding = padding,
        popBackStack = popBackStack,
        pagingItems = pagingItems,
        imageRatioMap = imageRatioMap,
        onUpdateRatio = { page, newRatio -> imageRatioMap[page] = newRatio },
        postId = postId
    )
}

@Composable
private fun DetailMyScreen(
    pagingItems: LazyPagingItems<MyPost>,
    postId: String = "",
    imageRatioMap: Map<Int, Float> = mapOf(),
    onUpdateRatio: (Int, Float) -> Unit = { _, _ -> },
    onShowReelsMode: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    padding: PaddingValues = PaddingValues()
) {
    val pagerState = rememberPagerState { pagingItems.itemCount }

    LaunchedEffect(postId, pagingItems.itemSnapshotList.items) {
        val index = pagingItems.itemSnapshotList.items.indexOfFirst { it.postId == postId }
        
        if (index >= 0) {
            pagerState.scrollToPage(index)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(padding)
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
                imageRatio = imageRatioMap[page] ?: (4f / 5f),
                onUpdateRatio = { onUpdateRatio(page, it) },
                onShowReelsMode = onShowReelsMode,
            )
        }
    }
}

@Composable
private fun DetailContent(
    post: MyPost,
    imageRatio: Float = 4f / 5f,
    onUpdateRatio: (Float) -> Unit = {},
    onShowReelsMode: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize()
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
                imageRatio = imageRatio,
                onClick = { onShowReelsMode(post.imageUrl) },
                updateImageRatio = onUpdateRatio
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