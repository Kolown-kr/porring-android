package com.kolown.porring.feature.my

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.kolown.porring.core.ui.component.GalleryItem
import com.kolown.porring.core.ui.component.RestrictedLoginContent
import com.kolown.porring.feature.my.component.PageItemFooter
import kotlinx.coroutines.delay

@Composable
internal fun MyRoute(
    viewModel: MyViewModel = hiltViewModel(),
    padding: PaddingValues = PaddingValues(),
    navigateToLogin: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToDetail: () -> Unit,
) {
    val pagingItems = viewModel.galleryFlow.collectAsLazyPagingItems()
    val isLoggedIn = viewModel.loginState.collectAsStateWithLifecycle(initialValue = true)
    val isDeleteSuccess by viewModel.isDeleteSuccess.collectAsStateWithLifecycle()

    if (isLoggedIn.value) {
        LaunchedEffect(Unit) {
            viewModel.setUserId()
        }
        MyScreen(
            isDeleteSuccess = isDeleteSuccess,
            navigateToSetting = navigateToSetting,
            navigateToDetailMy = navigateToDetail,
            setPage = viewModel::setPage,
            padding = padding,
            pagingItems = pagingItems,
            deletePost = viewModel::deletePost,
        )
    } else {
        RestrictedLoginContent(navigateToLogin)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyScreen(
    isDeleteSuccess: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    pagingItems: LazyPagingItems<PostContentModel>,
    setPage: (Int) -> Unit = {},
    deletePost: (String) -> Unit = {},
    navigateToSetting: () -> Unit = {},
    navigateToDetailMy: () -> Unit = {},
) {
    val listState = rememberLazyStaggeredGridState()
    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        pagingItems.refresh()
    }

    LaunchedEffect(pagingItems.loadState) {
        isRefreshing = false
    }
    LaunchedEffect(pagingItems) {
        listState.scrollToItem(0)
    }
    LaunchedEffect(isDeleteSuccess) {
        pagingItems.refresh()
    }

    val scaleFraction = {
        if (isRefreshing) 1f
        else LinearOutSlowInEasing.transform(refreshState.distanceFraction).coerceIn(0f, 1f)
    }

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
            title = stringResource(R.string.string_my_gallery),
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
                deletePost = deletePost,
                navigateToDetailMy = navigateToDetailMy,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StateLazyGrid(
    listState: LazyStaggeredGridState,
    pagingItems: LazyPagingItems<PostContentModel>,
    deletePost: (String) -> Unit,
    navigateToDetailMy: () -> Unit,
    setPage: (Int) -> Unit
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
                                        onLongClickImage = { deletePost(it.postId) },
                                        onClickImage = {
                                            setPage(index)
                                            navigateToDetailMy()
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
private fun ErrorScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = Icons.Default.Warning, contentDescription = null
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.string_error), color = Color.Red
            )
        }
    }
}

