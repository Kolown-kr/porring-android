package com.kolown.porring.feature.my

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.kolown.porring.core.designsystem.component.PorringCenterAlignTopAppBar
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.ui.component.BetaPorringAlertDialog
import com.kolown.porring.core.ui.component.ErrorScreen
import com.kolown.porring.core.ui.component.LoadingScreen
import com.kolown.porring.core.ui.component.RestrictedLoginContent
import com.kolown.porring.core.ui.component.StateLazyGrid
import kotlinx.coroutines.delay

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
    var showErrorScreen by remember { mutableStateOf(false) }
    var deletingPostId: String? by rememberSaveable { mutableStateOf(null) }
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

    LaunchedEffect(isDeleteSuccess) {
        pagingItems.refresh()
    }
    LaunchedEffect(pagingItems.loadState) {
        isRefreshing = false
    }
    LaunchedEffect(pagingItems) {
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

    if (isLoggedIn) {
        viewModel.setUserId()

        MyScreen(
            pagingItems = pagingItems,
            title = stringResource(R.string.string_my_gallery),
            isRefreshing = isRefreshing,
            showErrorScreen = showErrorScreen,
            padding = padding,
            refreshState = refreshState,
            listState = listState,
            onRefresh = onRefresh,
            setPage = viewModel::setPage,
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
    pagingItems: LazyPagingItems<PostContentModel>,
    title: String = "",
    isRefreshing: Boolean = false,
    showErrorScreen: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    refreshState: PullToRefreshState = rememberPullToRefreshState(),
    listState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    onRefresh: () -> Unit = {},
    setPage: (Int) -> Unit = {},
    navigateToDetail: () -> Unit = {},
    navigateToSetting: () -> Unit = {},
    scaleFraction: () -> Float = { 1f },
    updateShowErrorScreen: (Boolean) -> Unit = {},
    updateDeletingPostId: (String) -> Unit = {}
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
                        StateLazyGrid(
                            listState = listState,
                            pagingItems = pagingItems,
                            navigateToDetail = navigateToDetail,
                            setPage = setPage,
                            onLongClick = updateDeletingPostId
                        )
                    }
                }
            }

            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        scaleX = scaleFraction()
                        scaleY = scaleFraction()
                    }
            ) {
                PullToRefreshDefaults.Indicator(
                    state = refreshState,
                    isRefreshing = isRefreshing
                )
            }
        }
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
}