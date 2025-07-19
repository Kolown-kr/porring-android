package com.kolown.porring.feature.home

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kolown.porring.core.designsystem.ui.theme.Background
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.model.Reaction
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.model.UiState
import com.kolown.porring.core.ui.component.BetaPorringAlertDialog
import com.kolown.porring.core.ui.component.CoilImage
import com.kolown.porring.core.ui.component.ErrorScreen
import com.kolown.porring.core.ui.component.FollowDialog
import com.kolown.porring.core.ui.component.LoadingScreen
import com.kolown.porring.core.ui.component.PullToRefreshColumn
import com.kolown.porring.core.ui.component.ReactionGroup
import com.kolown.porring.core.ui.component.ReactionSelector
import com.kolown.porring.core.ui.component.toImage
import com.kolown.porring.core.ui.compositionlocal.LocalPaddingValues
import com.kolown.porring.core.ui.compositionlocal.LocalSnackBarBridge
import com.kolown.porring.core.ui.model.PostUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeRoute(
    padding: PaddingValues = LocalPaddingValues.current,
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToDetail: () -> Unit = {},
    navigateToTheir: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var followPostUiModel by remember { mutableStateOf<PostUiModel?>(null) }
    val snackBarBridge = LocalSnackBarBridge.current

    var isRefreshing by remember { mutableStateOf(false) }
    var isShowErrorScreen by remember { mutableStateOf(false) }
    val refreshState = rememberPullToRefreshState()

    val scaleFraction = {
        if (isRefreshing) 1f
        else LinearOutSlowInEasing.transform(refreshState.distanceFraction).coerceIn(0f, 1f)
    }

    val onRefresh: () -> Unit = {
        isRefreshing = true
        viewModel.refreshItems()
    }

    LaunchedEffect(viewModel.loggedInEvent) {
        viewModel.loggedInEvent.collect {
            snackBarBridge.postSnackBarEvent(SnackBarEvent.LoginRequired())
        }
    }

    LaunchedEffect(viewModel.followEvent) {
        viewModel.followEvent.collect { post ->
            followPostUiModel = post
        }
    }

    LaunchedEffect(uiState) {
        isRefreshing = false

        isShowErrorScreen = when (uiState) {
            is UiState.Failure -> true
            else -> false
        }
    }

    followPostUiModel?.let { post ->
        if (post.isFollowing) {
            BetaPorringAlertDialog(
                title = stringResource(R.string.string_unfollow),
                description = stringResource(R.string.string_unfollow_description),
                dismissText = stringResource(R.string.string_cancel),
                confirmText = stringResource(R.string.string_confirm),
                onConfirm = { viewModel.unFollowUser(post.authorId) },
                onDismissRequest = { followPostUiModel = null }
            )
        } else {
            FollowDialog(
                onClickConfirm = { viewModel.registerFollow(post.authorId, it) },
                onClickCancel = { followPostUiModel = null }
            )
        }
    }

    HomeScreen(
        padding = padding,
        uiState = uiState,
        isRefreshing = isRefreshing,
        isShowErrorScreen = isShowErrorScreen,
        scaleFraction = scaleFraction,
        refreshState = refreshState,
        updateIsShowErrorScreen = { isShowErrorScreen = it },
        navigateToDetail = {
            viewModel.onClickItem(it)
            navigateToDetail()
        },
        navigateToTheir = navigateToTheir,
        onRefresh = onRefresh,
        onFollowClick = viewModel::onFollowClick,
        onReactionClick = viewModel::onReactionClick
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    padding: PaddingValues = PaddingValues(),
    uiState: UiState<List<PostUiModel>> = UiState.Loading,
    isRefreshing: Boolean = false,
    isShowErrorScreen: Boolean = false,
    scaleFraction: () -> Float = { 1f },
    refreshState: PullToRefreshState = rememberPullToRefreshState(),
    updateIsShowErrorScreen: (Boolean) -> Unit = {},
    navigateToDetail: (PostUiModel) -> Unit = {},
    navigateToTheir: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    onFollowClick: (PostUiModel) -> Unit = {},
    onReactionClick: (String, Reaction) -> Unit = { _, _ -> }
) {
    PullToRefreshColumn(
        padding = padding,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        refreshState = refreshState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        scaleFraction = scaleFraction,
        topBar = { },
    ) {
        when {
            isShowErrorScreen -> {
                ErrorScreen()
            }

            uiState is UiState.Failure -> {
                updateIsShowErrorScreen(false)
                ErrorScreen()
            }

            uiState is UiState.Loading -> {
                LoadingScreen()
            }

            uiState is UiState.Success -> {
                val images = uiState.data
                val pagerState = rememberPagerState(pageCount = { images.size })
                updateIsShowErrorScreen(false)

                Box {
                    HomeContent(
                        postUiModels = images,
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .verticalScroll(rememberScrollState()),
                        pagerState = pagerState,
                        onReactionClick = onReactionClick,
                        onImageClick = navigateToDetail,
                        onGalleryClick = navigateToTheir,
                        onFollowClick = onFollowClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    postUiModels: List<PostUiModel>,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(pageCount = { 1 }),
    onReactionClick: (String, Reaction) -> Unit = { _, _ -> },
    onImageClick: (PostUiModel) -> Unit = {},
    onGalleryClick: (String) -> Unit = {},
    onFollowClick: (PostUiModel) -> Unit = {},
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) { page ->
        val post = postUiModels[page]

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            Spacer(Modifier.weight(1f))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                CoilImage(
                    imageUrl = post.imageUrl,
                    imageRatio = post.imageRatio,
                    onClick = { onImageClick(post) }
                )
            }

            Spacer(Modifier.height(20.dp))

            EventRow(
                postUiModel = post,
                isFollowed = post.isFollowing,
                activatedReaction = post.myReaction,
                onReactionClick = { onReactionClick(post.postId, it) },
                onGalleryClick = onGalleryClick,
                onFollowClick = onFollowClick
            )

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun EventRow(
    postUiModel: PostUiModel = PostUiModel.EMPTY,
    isFollowed: Boolean = false,
    activatedReaction: Reaction? = null,
    onReactionClick: (Reaction) -> Unit = {},
    onGalleryClick: (String) -> Unit = {},
    onFollowClick: (PostUiModel) -> Unit = {}
) {
    var isExpand by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(40.dp),
            onClick = { isExpand = true }
        ) {
            Icon(
                imageVector = if (activatedReaction != null) {
                    ImageVector.vectorResource(activatedReaction.toImage())
                } else {
                    ImageVector.vectorResource(com.kolown.porring.core.ui.R.drawable.ic_reaction_unselected)
                },
                tint = Primary,
                contentDescription = stringResource(com.kolown.porring.core.ui.R.string.string_reaction_button),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        ReactionGroup(
            reactions = postUiModel.reactions,
            myReaction = postUiModel.myReaction
        )

        DropdownMenu(
            modifier = Modifier
                .width(352.dp),
            expanded = isExpand,
            containerColor = Color.Transparent,
            shape = CircleShape,
            onDismissRequest = { isExpand = false }
        ) {
            ReactionSelector(
                activatedReaction = activatedReaction,
                selectedReaction = onReactionClick,
                onDismiss = { isExpand = false }
            )
        }

        Spacer(Modifier.weight(1f))

        HomeButton(
            onClick = { onGalleryClick(postUiModel.authorId) },
            imageVector = ImageVector.vectorResource(com.kolown.porring.core.ui.R.drawable.ic_detail_gallary),
        )

        Spacer(Modifier.width(12.dp))

        HomeButton(
            onClick = { onFollowClick(postUiModel) },
            imageVector = ImageVector.vectorResource(com.kolown.porring.core.ui.R.drawable.ic_detail_follow),
            contentColor = if (isFollowed) Background else Primary,
            backgroundColor = if (isFollowed) Primary else Background
        )
    }
}

@Composable
private fun HomeButton(
    imageVector: ImageVector,
    contentColor: Color = Primary,
    backgroundColor: Color = Background,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    HomeContent(listOf(PostUiModel.EMPTY, PostUiModel.EMPTY, PostUiModel.EMPTY))
}
