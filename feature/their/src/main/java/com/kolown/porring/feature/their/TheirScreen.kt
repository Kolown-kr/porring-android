package com.kolown.porring.feature.their

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringCenterAlignTopAppBar
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.ui.component.StateLazyGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TheirRoute(
    followerId: String,
    padding: PaddingValues = PaddingValues(),
    viewModel: TheirViewModel = hiltViewModel(),
    popBackStack: () -> Unit = {},
    navigateToDetailTheir: () -> Unit = {},
) {
    val pagingItems = viewModel.galleryFlow.collectAsLazyPagingItems()
    val followerName by viewModel.followerName.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }
    val listState = rememberLazyStaggeredGridState()
    val refreshState = rememberPullToRefreshState()

    val title = if (followerName.isBlank()) {
        stringResource(R.string.string_empty)
    } else {
        stringResource(R.string.string_others_gallery_name, followerName)
    }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        pagingItems.refresh()
    }

    val scaleFraction = {
        if (isRefreshing) 1f
        else LinearOutSlowInEasing.transform(refreshState.distanceFraction).coerceIn(0f, 1f)
    }

    LaunchedEffect(followerId) {
        viewModel.setFollowerName(followerId)
    }
    LaunchedEffect(pagingItems.loadState) {
        isRefreshing = false
    }
    LaunchedEffect(pagingItems) {
        listState.scrollToItem(0)
    }

    TheirScreen(
        pagingItems = pagingItems,
        title = title,
        isRefreshing = isRefreshing,
        padding = padding,
        refreshState = refreshState,
        listState = listState,
        onRefresh = onRefresh,
        setPage = viewModel::setPage,
        popBackStack = popBackStack,
        navigateToDetail = navigateToDetailTheir,
        scaleFraction = scaleFraction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TheirScreen(
    pagingItems: LazyPagingItems<PostContentModel>,
    title: String = "",
    isRefreshing: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    refreshState: PullToRefreshState = rememberPullToRefreshState(),
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onRefresh: () -> Unit = {},
    setPage: (Int) -> Unit = {},
    popBackStack: () -> Unit = {},
    navigateToDetail: () -> Unit = {},
    scaleFraction: () -> Float = { 1f },
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .pullToRefresh(
                state = refreshState,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh
            )
    ) {
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            StateLazyGrid(
                listState = listState,
                longClickEnabled = false,
                pagingItems = pagingItems,
                navigateToDetail = navigateToDetail,
                setPage = setPage
            )
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        scaleX = scaleFraction()
                        scaleY = scaleFraction()
                    }
            ) {
                PullToRefreshDefaults.Indicator(state = refreshState, isRefreshing = isRefreshing)
            }
        }
    }
}