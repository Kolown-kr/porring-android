package com.kolown.porring.feature.follower

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringCenterAlignTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.Background
import com.kolown.porring.core.model.FollowWithThumbnail
import com.kolown.porring.core.ui.component.ErrorScreen
import com.kolown.porring.core.ui.component.FollowDialog
import com.kolown.porring.core.ui.component.LoadingScreen
import com.kolown.porring.core.ui.component.PullToRefreshColumn
import com.kolown.porring.core.ui.component.RestrictedLoginContent
import com.kolown.porring.feature.follower.component.PageItemFooter
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FollowerRoute(
    padding: PaddingValues = PaddingValues(),
    viewModel: FollowerViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToTheir: (String) -> Unit,
) {
    val pagingItems = viewModel.followerItems.collectAsLazyPagingItems()
    val loginState by viewModel.isLoggedIn.collectAsStateWithLifecycle(false)
    val pagerState = rememberLazyListState()

    var followWithThumbnail by remember { mutableStateOf<FollowWithThumbnail?>(null) }
    var showErrorScreen by remember { mutableStateOf(false) }

    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()

    val onRefresh: () -> Unit = {
        isRefreshing = true
        pagingItems.refresh()
    }

    val scaleFraction = {
        if (isRefreshing) 1f
        else LinearOutSlowInEasing.transform(refreshState.distanceFraction).coerceIn(0f, 1f)
    }

    LaunchedEffect(pagingItems.loadState.refresh) {
        isRefreshing = false
        if (pagingItems.loadState.refresh == LoadState.Loading) {
            delay(7000)
            showErrorScreen = true
        } else {
            showErrorScreen = false
        }
    }

    followWithThumbnail?.let {
        FollowDialog(
            isAddFollow = false,
            onClickConfirm = { },
            onClickCancel = { followWithThumbnail = null }
        )
    }

    if (loginState) {
        FollowerScreen(
            pagingItems = pagingItems,
            padding = padding,
            isRefreshing = isRefreshing,
            showErrorScreen = showErrorScreen,
            pagerState = pagerState,
            refreshState = refreshState,
            onRefresh = onRefresh,
            scaleFraction = scaleFraction,
            navigateToTheir = navigateToTheir,
            updateFollowerThumbnail = { followWithThumbnail = it }
        )
    } else {
        RestrictedLoginContent(
            navigateToLogin = navigateToLogin,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FollowerScreen(
    pagingItems: LazyPagingItems<FollowWithThumbnail>,
    padding: PaddingValues = PaddingValues(),
    isRefreshing: Boolean = false,
    showErrorScreen: Boolean = false,
    pagerState: LazyListState = rememberLazyListState(),
    refreshState: PullToRefreshState = rememberPullToRefreshState(),
    onRefresh: () -> Unit = {},
    scaleFraction: () -> Float = { 1f },
    navigateToTheir: (String) -> Unit = {},
    updateFollowerThumbnail: (FollowWithThumbnail) -> Unit = {}
) {
    PullToRefreshColumn(
        padding = padding,
        refreshState = refreshState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        scaleFraction = scaleFraction,
        topBar = { Spacer(modifier = Modifier.height(16.dp)) }
    ) {
        when {
            showErrorScreen -> {
                ErrorScreen()
            }

            pagingItems.loadState.refresh is LoadState.Error -> {
                ErrorScreen()
            }

            pagingItems.loadState.refresh is LoadState.Loading -> {
                LoadingScreen()
            }

            pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount == 0 -> {
                NoFollowerScreen()
            }

            pagingItems.loadState.refresh is LoadState.NotLoading && pagingItems.itemCount != 0 -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    state = pagerState,
                ) {
                    items(pagingItems.itemCount) { index ->
                        pagingItems[index]?.let {
                            FollowContent(
                                followWithThumbnail = it,
                                navigateToTheir = { navigateToTheir(it.id) },
                                updateFollowerThumbnail = updateFollowerThumbnail
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (pagingItems.loadState.append !is LoadState.NotLoading) {
                        item(key = "") {
                            PageItemFooter(loadState = pagingItems.loadState.append) {
                                pagingItems.retry()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NoFollowerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 100.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.string_not_follow), color = Color.Gray)
    }
}
