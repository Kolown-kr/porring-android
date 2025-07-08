package com.kolown.porring.feature.their

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringCenterAlignTopAppBar
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.ui.component.ErrorScreen
import com.kolown.porring.core.ui.component.PullToRefreshColumn
import com.kolown.porring.core.ui.component.StateLazyGrid
import com.kolown.porring.core.ui.compositionlocal.LocalPaddingValues
import com.kolown.porring.core.ui.model.PostUiModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TheirRoute(
    padding: PaddingValues = LocalPaddingValues.current,
    viewModel: TheirViewModel = hiltViewModel(),
    popBackStack: () -> Unit = {},
    navigateToDetail: (String, String) -> Unit = { _, _ -> },
) {
    val pagingItems = viewModel.pagingItems.collectAsLazyPagingItems()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }
    var showErrorScreen by remember { mutableStateOf(false) }
    val listState = rememberLazyStaggeredGridState()
    val refreshState = rememberPullToRefreshState()

    val onRefresh: () -> Unit = {
        isRefreshing = true
        pagingItems.refresh()
    }

    val scaleFraction = {
        if (isRefreshing) 1f
        else LinearOutSlowInEasing.transform(refreshState.distanceFraction).coerceIn(0f, 1f)
    }

    LaunchedEffect(pagingItems.loadState) {
        isRefreshing = false
    }
    LaunchedEffect(pagingItems.loadState.refresh) {
        listState.scrollToItem(0)
    }
    LaunchedEffect(pagingItems.loadState.refresh) {
        if (pagingItems.loadState.refresh == LoadState.Loading) {
            delay(7000)
            showErrorScreen = true
        } else {
            showErrorScreen = false
        }
    }

    TheirScreen(
        pagingItems = pagingItems,
        title = uiState.title,
        isRefreshing = isRefreshing,
        showErrorScreen = false,
        padding = padding,
        refreshState = refreshState,
        listState = listState,
        onRefresh = onRefresh,
        popBackStack = popBackStack,
        navigateToDetail = { navigateToDetail(it.authorId, it.postId) },
        scaleFraction = scaleFraction,
        updateShowErrorScreen = { showErrorScreen = it }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TheirScreen(
    pagingItems: LazyPagingItems<PostUiModel>,
    title: String = "",
    isRefreshing: Boolean = false,
    showErrorScreen: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    refreshState: PullToRefreshState = rememberPullToRefreshState(),
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onRefresh: () -> Unit = {},
    setPage: (Int) -> Unit = {},
    popBackStack: () -> Unit = {},
    navigateToDetail: (PostUiModel) -> Unit = {},
    scaleFraction: () -> Float = { 1f },
    updateShowErrorScreen: (Boolean) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        PullToRefreshColumn(
            refreshState = refreshState,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.padding(top = padding.calculateTopPadding()),
            scaleFraction = scaleFraction,
            topBar = {
                PorringCenterAlignTopAppBar(
                    title = title,
                    navigationIcon = {
                        PorringIconButton(
                            icon = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                            onClick = popBackStack,
                            contentDescription = stringResource(R.string.string_go_back)
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
                    CompositionLocalProvider(
                        LocalOverscrollConfiguration provides null
                    ) {
                        StateLazyGrid(
                            padding = padding,
                            listState = listState,
                            longClickEnabled = false,
                            pagingItems = pagingItems,
                            navigateToDetail = navigateToDetail,
                            setPage = setPage
                        )
                    }
                }

                pagingItems.loadState.refresh is LoadState.NotLoading -> {
                    CompositionLocalProvider(
                        LocalOverscrollConfiguration provides null
                    ) {
                        StateLazyGrid(
                            padding = padding,
                            listState = listState,
                            longClickEnabled = false,
                            pagingItems = pagingItems,
                            navigateToDetail = navigateToDetail,
                            setPage = setPage
                        )
                    }
                }
            }
        }
    }
}