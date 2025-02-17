package com.kolown.porring.feature.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.model.UiState
import com.kolown.porring.core.ui.component.FollowDialog
import com.kolown.porring.core.ui.component.LocalSnackBarBridge
import com.kolown.porring.core.ui.component.UnfollowCheckDialog
import com.kolown.porring.feature.home.component.IconButtonGroup
import com.kolown.porring.feature.home.component.LottieFireWorkAnimation
import com.kolown.porring.feature.home.component.RandomImage
import com.kolown.porring.feature.home.component.ReactionDialog
import com.kolown.porring.feature.home.component.ReactionGroup
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeRoute(
    padding: PaddingValues = PaddingValues(),
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToDetail: () -> Unit = {},
    navigateToTheir: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle(false)
    var isReactionDialogVisible by remember { mutableStateOf(false) }
    var isShowErrorScreen by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()
    val scaleFraction = {
        if (isRefreshing) 1f
        else LinearOutSlowInEasing.transform(refreshState.distanceFraction).coerceIn(0f, 1f)
    }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.changeLoading()
    }

    LaunchedEffect(uiState) {
        if (uiState is UiState.Loading) {
            isRefreshing = false
            delay(7000)
            isShowErrorScreen = true
        } else {
            isShowErrorScreen = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .pointerInput(isReactionDialogVisible) {
                if (isReactionDialogVisible) {
                    detectTapGestures { isReactionDialogVisible = false }
                }
            }
            .pullToRefresh(
                state = refreshState,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            when {
                isShowErrorScreen -> {
                    ErrorScreen()
                }

                uiState is UiState.Failure -> {
                    isShowErrorScreen = false
                    ErrorScreen()
                }

                uiState is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp), color = Primary)
                    }
                }

                uiState is UiState.Success -> {
                    val images = (uiState as UiState.Success<List<PostContentModel>>).data
                    val pagerState = rememberPagerState(pageCount = { images.size })
                    isShowErrorScreen = false

                    if(images.isEmpty()) {
                        viewModel.changeLoading()
                    } else {
                        HomeScreen(
                            modifier = Modifier
                                .fillMaxSize()
                                .align(Alignment.Center)
                                .verticalScroll(rememberScrollState()),
                            isLoggedIn = loginState,
                            isReactionDialogVisible = isReactionDialogVisible,
                            imageItems = images,
                            pagerState = pagerState,
                            navigateToDetail = navigateToDetail,
                            navigateToTheir = navigateToTheir,
                            updateIsReactionDialogVisible = { isReactionDialogVisible = !isReactionDialogVisible },
                            onSelectReaction =  viewModel::selectReaction,
                            onUnfollowClick = viewModel::unFollowUser,
                            onFollowClick = viewModel::followUser
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
                PullToRefreshDefaults.Indicator(state = refreshState, isRefreshing = isRefreshing)
            }
        }
    }
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean = false,
    isReactionDialogVisible: Boolean = false,
    imageItems: List<PostContentModel> = emptyList(),
    pagerState: PagerState = rememberPagerState(pageCount = { 1 }),
    navigateToDetail: () -> Unit = {},
    navigateToTheir: (String) -> Unit = {},
    updateIsReactionDialogVisible: () -> Unit = {},
    onSelectReaction: (PostContentModel, Reactions) -> Unit = { _, _ -> },
    onUnfollowClick: (String) -> Unit = {},
    onFollowClick: (String, String) -> Unit = { _, _ -> },
    fetchDetailFirst: (PostContentModel) -> Unit = {},
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ImageCard(
                isLoggedIn = isLoggedIn,
                imageItem = imageItems[page],
                isReactionDialogVisible = isReactionDialogVisible,
                onFollowClick = onFollowClick,
                onUnfollowClick = onUnfollowClick,
                onChangeReactionDialogVisibility = updateIsReactionDialogVisible,
                navigateToTheir = navigateToTheir,
                onSelectReaction = { reaction -> onSelectReaction(imageItems[page], reaction) },
                fetchDetailFirst = fetchDetailFirst,
                navigateToDetail = navigateToDetail
            )
            LottieFireWorkAnimation(
                modifier = Modifier.align(Alignment.TopEnd),
                reactions = imageItems[page].reactions,
                myReaction = imageItems[page].myReaction
            )
        }
    }
}

@Composable
private fun ImageCard(
    isLoggedIn: Boolean,
    imageItem: PostContentModel,
    isReactionDialogVisible: Boolean,
    onFollowClick: (String, String) -> Unit,
    onUnfollowClick: (String) -> Unit,
    onSelectReaction: (Reactions) -> Unit,
    onChangeReactionDialogVisibility: () -> Unit,
    navigateToTheir: (String) -> Unit,
    fetchDetailFirst: (PostContentModel) -> Unit,
    navigateToDetail: () -> Unit,
) {
    val likedImageVector =
        if (imageItem.myReaction == null) Icons.Outlined.FavoriteBorder else Icons.Outlined.Favorite
    var isFollowDialogVisible by remember { mutableStateOf(false) }
    var isFirstRenderer by remember { mutableStateOf(true) }
    val sizeAnimation = remember { Animatable(1f) }

    val snackBarBridge = LocalSnackBarBridge.current

    LaunchedEffect(imageItem.myReaction) {
        if(isFirstRenderer) {
            isFirstRenderer = false
            return@LaunchedEffect
        }
        sizeAnimation.animateTo(
            targetValue = 1f,
            animationSpec = keyframes {
                durationMillis = 400
                1.4f at 100
                1.1f at 200
                1.2f at 300
                1f at 400
            }
        )
    }

    Column {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 40.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            RandomImage(
                imageItem.imageUrl,
                onClickImage = {
                    fetchDetailFirst(imageItem)
                    navigateToDetail()
                }
            )
            ReactionGroup(
                modifier = Modifier.align(Alignment.TopEnd),
                reactions = imageItem.reactions
            )
            IconButtonGroup(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                imageItem = imageItem,
                navigateToTheir = { navigateToTheir(imageItem.authorId) },
                onFollowClick = {
                    if (isLoggedIn) {
                        isFollowDialogVisible = true
                    } else {
                        snackBarBridge.postSnackBarEvent(SnackBarEvent.LoginRequired())
                    }
                }
            )
            if (isReactionDialogVisible) {
                ReactionDialog(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    selectedReaction = imageItem.myReaction,
                    onClick = onSelectReaction,
                    onDismiss = onChangeReactionDialogVisibility
                )
            }
        }

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            onClick = {
                if (isLoggedIn) onChangeReactionDialogVisibility()
                else snackBarBridge.postSnackBarEvent(
                    SnackBarEvent.LoginRequired()
                )
            },
        ) {
            Icon(
                modifier = Modifier.size(30.dp * sizeAnimation.value),
                imageVector = likedImageVector,
                contentDescription = null,
                tint = Primary
            )
        }
        if (isFollowDialogVisible) {
            if(imageItem.isFollower) {
                // TODO Exchange to AlertDialog
                UnfollowCheckDialog(
                    title = stringResource(R.string.string_unfollow),
                    description = stringResource(R.string.string_unfollow_description),
                    dismissText = stringResource(R.string.string_cancel),
                    confirmText = stringResource(R.string.string_confirm),
                    onDismissRequest = { isFollowDialogVisible = false },
                    onConfirm = { onUnfollowClick(imageItem.authorId) }
                )
            } else {
                FollowDialog(
                    onClickCancel = { isFollowDialogVisible = false },
                    onClickConfirm = { name ->
                        onFollowClick(imageItem.authorId, name)
                    }
                )
            }
        }
    }
}

// todo dev 머지되면 삭제하고 ui에서 가져와서 사용
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

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    HomeScreen()
}
