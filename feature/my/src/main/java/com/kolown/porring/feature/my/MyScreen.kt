package com.kolown.porring.feature.my

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringCenterAlignTopAppBar
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.ui.component.RestrictedLoginContent
import com.kolown.porring.core.ui.component.StateLazyGrid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyRoute(
    viewModel: MyViewModel = hiltViewModel(),
    padding: PaddingValues = PaddingValues(),
    navigateToLogin: () -> Unit = {},
    navigateToSetting: () -> Unit = {},
    navigateToDetail: () -> Unit = {},
) {
    val pagingItems = viewModel.galleryFlow.collectAsLazyPagingItems()
    val isDeleteSuccess by viewModel.isDeleteSuccess.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.loginState.collectAsStateWithLifecycle(initialValue = true)
    var isRefreshing by remember { mutableStateOf(false) }
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

    LaunchedEffect(isDeleteSuccess) {
        pagingItems.refresh()
    }
    LaunchedEffect(pagingItems.loadState) {
        isRefreshing = false
    }
    LaunchedEffect(pagingItems) {
        listState.scrollToItem(0)
    }

    if (isLoggedIn) {
        viewModel.setUserId()

        MyScreen(
            pagingItems = pagingItems,
            title = stringResource(R.string.string_my_gallery),
            isRefreshing = isRefreshing,
            padding = padding,
            refreshState = refreshState,
            listState = listState,
            onRefresh = onRefresh,
            setPage = viewModel::setPage,
            navigateToDetail = navigateToDetail,
            navigateToSetting = navigateToSetting,
            scaleFraction = scaleFraction,
            onItemLongClick = viewModel::deletePost,
        )
    } else {
        RestrictedLoginContent(navigateToLogin)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyScreen(
    pagingItems: LazyPagingItems<PostContentModel>,
    title: String = "",
    isRefreshing: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    refreshState: PullToRefreshState = rememberPullToRefreshState(),
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onRefresh: () -> Unit = {},
    setPage: (Int) -> Unit = {},
    navigateToDetail: () -> Unit = {},
    navigateToSetting: () -> Unit = {},
    scaleFraction: () -> Float = { 1f },
    onItemLongClick: (String) -> Unit = {},
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
            trailingIcon = {
                PorringIconButton(
                    icon = Icons.Default.Settings,
                    onClick = navigateToSetting,
                    contentDescription = stringResource(R.string.string_setting)
                )
            }
        )
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            StateLazyGrid(
                listState = listState,
                pagingItems = pagingItems,
                navigateToDetail = navigateToDetail,
                setPage = setPage,
                onLongClick = onItemLongClick
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
