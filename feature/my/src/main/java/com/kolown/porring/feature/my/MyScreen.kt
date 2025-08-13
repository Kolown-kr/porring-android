package com.kolown.porring.feature.my

import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringCenterAlignTopAppBar
import com.kolown.porring.core.designsystem.component.button.PorringIconButton
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.model.MyPost
import com.kolown.porring.core.ui.component.BetaPorringAlertDialog
import com.kolown.porring.core.ui.component.CoilImage
import com.kolown.porring.core.ui.component.ErrorScreen
import com.kolown.porring.core.ui.component.LoadingScreen
import com.kolown.porring.core.ui.component.PageItemFooter
import com.kolown.porring.core.ui.component.PullToRefreshColumn
import com.kolown.porring.core.ui.component.RestrictedLoginContent
import com.kolown.porring.core.ui.compositionlocal.LocalPaddingValues
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyRoute(
    navigateToLogin: () -> Unit = {},
    navigateToSetting: () -> Unit = {},
    navigateToDetail: (Int) -> Unit = { _ -> },
    padding: PaddingValues = LocalPaddingValues.current,
    viewModel: MyViewModel = hiltViewModel(),
) {
    val pagingItems = viewModel.pagingItems.collectAsLazyPagingItems()

    val isLoggedIn by viewModel.loginState.collectAsStateWithLifecycle(initialValue = true)
    var isRefreshing by remember { mutableStateOf(false) }
    var showErrorScreen by remember { mutableStateOf(false) }
    var deletingPostId: String? by rememberSaveable { mutableStateOf(null) }
    val listState = rememberLazyStaggeredGridState()
    val refreshState = rememberPullToRefreshState()

    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.refresh()
    }

    val scaleFraction = {
        if (isRefreshing) 1f
        else LinearOutSlowInEasing.transform(refreshState.distanceFraction).coerceIn(0f, 1f)
    }

    deletingPostId?.let {
        BetaPorringAlertDialog(
            title = stringResource(R.string.string_post_delete_title),
            description = stringResource(R.string.string_question_delete),
            dismissText = stringResource(R.string.string_cancel),
            confirmText = stringResource(R.string.string_remove),
            onDismissRequest = { deletingPostId = null },
            onConfirm = {
                viewModel.deletePost(it)
                deletingPostId = null
            }
        )
    }

    LaunchedEffect(pagingItems.loadState) {
        isRefreshing = false
    }

    LaunchedEffect(pagingItems.loadState.refresh) {
        if (pagingItems.loadState.refresh == LoadState.Loading) {
            delay(7000)
            showErrorScreen = true
        } else {
            showErrorScreen = false
        }
    }

    if (isLoggedIn) {
        MyScreen(
            pagingItems = pagingItems,
            title = stringResource(R.string.string_my_gallery),
            isRefreshing = isRefreshing,
            showErrorScreen = showErrorScreen,
            padding = padding,
            refreshState = refreshState,
            listState = listState,
            onRefresh = onRefresh,
            navigateToDetail = navigateToDetail,
            navigateToSetting = navigateToSetting,
            scaleFraction = scaleFraction,
            updateShowErrorScreen = { showErrorScreen = it },
            updateDeletingPostId = { deletingPostId = it }
        )
    } else {
        RestrictedLoginContent(navigateToLogin)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun MyScreen(
    pagingItems: LazyPagingItems<MyPost>,
    title: String = "",
    isRefreshing: Boolean = false,
    showErrorScreen: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    refreshState: PullToRefreshState,
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onRefresh: () -> Unit = {},
    navigateToDetail: (Int) -> Unit = {},
    navigateToSetting: () -> Unit = {},
    scaleFraction: () -> Float = { 1f },
    updateShowErrorScreen: (Boolean) -> Unit = {},
    updateDeletingPostId: (String) -> Unit = {}
) {
    PullToRefreshColumn(
        refreshState = refreshState,
        isRefreshing = isRefreshing,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = padding.calculateTopPadding()),
        onRefresh = onRefresh,
        scaleFraction = scaleFraction,
        topBar = {
            PorringCenterAlignTopAppBar(
                title = title,
                trailingIcon = {
                    PorringIconButton(
                        icon = Icons.Default.Settings,
                        onClick = navigateToSetting,
                        contentDescription = stringResource(R.string.string_setting)
                    )
                }
            )
        }
    ) {
        when {
            showErrorScreen -> {
                ErrorScreen()
            }

            pagingItems.loadState.refresh is LoadState.Error -> {
                updateShowErrorScreen(false)
                ErrorScreen()
            }

            pagingItems.loadState.refresh is LoadState.Loading -> {
                updateShowErrorScreen(false)
                LoadingScreen()
            }

            pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0 -> {
                NoItemScreen()
            }

            pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount != 0 -> {
                CompositionLocalProvider(
                    LocalOverscrollConfiguration provides null
                ) {
                    MyContent(
                        listState = listState,
                        pagingItems = pagingItems,
                        navigateToDetail = navigateToDetail,
                        bottomPadding = padding.calculateBottomPadding(),
                        onLongClick = updateDeletingPostId
                    )
                }
            }
        }
    }
}

@Composable
private fun MyContent(
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    longClickEnabled: Boolean = true,
    pagingItems: LazyPagingItems<MyPost>,
    bottomPadding: Dp,
    navigateToDetail: (Int) -> Unit = {},
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
                    post = pagingItem,
                    longClickEnabled = longClickEnabled,
                    onLongClickImage = { onLongClick(pagingItem.postId) },
                    onClickImage = { navigateToDetail(index) }
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

        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(Modifier.height(bottomPadding))
        }
    }
}

@Composable
private fun GalleryItem(
    post: MyPost,
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
            imageUrl = post.imageUrl,
            imageRatio = post.imageRatio,
        )
    }
}

@Composable
private fun NoItemScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(100.dp),
                painter = painterResource(com.kolown.porring.core.designsystem.R.drawable.ic_question_mark),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = stringResource(R.string.string_no_post),
                color = Primary
            )
        }
    }
}