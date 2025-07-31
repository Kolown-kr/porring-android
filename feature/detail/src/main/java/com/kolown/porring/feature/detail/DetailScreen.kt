package com.kolown.porring.feature.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.component.button.PorringIconButton
import com.kolown.porring.core.designsystem.icon.PorringIcons
import com.kolown.porring.core.designsystem.ui.theme.Background
import com.kolown.porring.core.designsystem.ui.theme.DarkModeScreen
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.PrimaryDark
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.Reaction
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.core.ui.component.BetaPorringAlertDialog
import com.kolown.porring.core.ui.component.CoilImage
import com.kolown.porring.core.ui.component.FollowDialog
import com.kolown.porring.core.ui.component.reaction.ReactionGroup
import com.kolown.porring.core.ui.component.reaction.ReactionSelector
import com.kolown.porring.core.ui.component.reaction.getIcon
import com.kolown.porring.core.ui.compositionlocal.LocalSnackBarBridge
import com.kolown.porring.core.ui.model.PostUiModel
import com.kolown.porring.feature.detail.component.FullScreenEffect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
internal fun DetailRoute(
    viewModel: DetailViewModel = hiltViewModel(),
    navigateToTheir: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
) {
    val currentGalleryType by viewModel.currentGalleryType.collectAsState()

    val posts = viewModel.posts.collectAsLazyPagingItems()
    val initialPost by viewModel.initialPost.collectAsState()
    var isPagingItemLoading by remember { mutableStateOf(false) }

    var followPostUiModel by remember { mutableStateOf<PostUiModel?>(null) }
    var reelsModePostUrl by remember { mutableStateOf<String?>(null) }
    val snackBarBridge = LocalSnackBarBridge.current

    val pagerState = rememberPagerState { posts.itemCount }

    LaunchedEffect(Unit) {
        snapshotFlow { posts.loadState.refresh }
            .filter { it is LoadState.NotLoading }
            .first()
            .let { isPagingItemLoading = true }
    }

    LaunchedEffect(initialPost) {
        if (initialPost == null) return@LaunchedEffect

        snapshotFlow { posts.itemSnapshotList.items.indexOfFirst { it.postId == initialPost!!.postId } }
            .filter { it >= 0 }
            .first()
            .let { pagerState.scrollToPage(it) }
    }


    LaunchedEffect(pagerState.currentPage) {
        val state = PageState(pagerState.currentPage, pagerState.pageCount)

        viewModel.updatePage(state)
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

    followPostUiModel?.let { post ->
        if (post.isFollowing) {
            BetaPorringAlertDialog(
                title = "팔로우 취소",
                description = "팔로우를 취소하시겠습니까?",
                dismissText = "취소",
                confirmText = "확인",
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

    DarkModeScreen {
        if (reelsModePostUrl != null) {
            val currentPost = posts[pagerState.currentPage]

            FullScreenEffect()
            BackHandler(onBack = { reelsModePostUrl = null })
            FocusScreen(
                postUrl = reelsModePostUrl ?: "",
                imageRatio = currentPost?.imageRatio ?: (4f / 5f),
                onDismiss = { reelsModePostUrl = null }
            )
        } else {
            DetailScreen(
                isItemsLoading = isPagingItemLoading,
                initialPost = initialPost ?: PostUiModel.EMPTY,
                posts = posts,
                pagerState = pagerState,
                galleryVisible = currentGalleryType == Route.Detail.Type.DEFAULT,
                onShowReelsMode = { reelsModePostUrl = it },
                onReactionClick = viewModel::onReactionClick,
                onGalleryClick = navigateToTheir,
                onFollowClick = viewModel::onFollowClick,
                popBackStack = popBackStack
            )
        }
    }
}

@Composable
private fun DetailScreen(
    isItemsLoading: Boolean = false,
    initialPost: PostUiModel = PostUiModel.EMPTY,
    posts: LazyPagingItems<PostUiModel>,
    pagerState: PagerState = rememberPagerState(initialPage = 0) { posts.itemCount },
    galleryVisible: Boolean = true,
    onShowReelsMode: (String) -> Unit = {},
    onReactionClick: (String, Reaction) -> Unit = { _, _ -> },
    onGalleryClick: (String) -> Unit = {},
    onFollowClick: (PostUiModel) -> Unit = {},
    popBackStack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        PorringTopAppBar(
            navigationIcon = {
                PorringIconButton(
                    icon = PorringIcons.Default.ArrowBack,
                    onClick = popBackStack,
                    contentDescription = stringResource(R.string.string_go_back),
                    color = Color.White
                )
            },
        )

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            key = { index ->
                posts[index]?.postId ?: "placeholder_$index"
            },
            beyondViewportPageCount = 3,
        ) { page: Int ->
            val post = if (isItemsLoading) posts[page] ?: return@HorizontalPager else initialPost

            DetailContent(
                postUiModel = post,
                galleryVisible = galleryVisible,
                onShowReelsMode = onShowReelsMode,
                onReactionClick = onReactionClick,
                onGalleryClick = onGalleryClick,
                onFollowClick = onFollowClick
            )
        }
    }
}

@Composable
private fun DetailContent(
    postUiModel: PostUiModel = PostUiModel.EMPTY,
    galleryVisible: Boolean = true,
    onShowReelsMode: (String) -> Unit = {},
    onReactionClick: (String, Reaction) -> Unit = { _, _ -> },
    onGalleryClick: (String) -> Unit = {},
    onFollowClick: (PostUiModel) -> Unit = {}
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
                imageUrl = postUiModel.imageUrl,
                imageRatio = postUiModel.imageRatio,
                onClick = { onShowReelsMode(postUiModel.imageUrl) },
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
                    text = postUiModel.description,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )

                if (postUiModel.tags.isNotEmpty()) {
                    Text(
                        text = buildString {
                            postUiModel.tags.forEachIndexed { index, tag ->
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

            ReactionGroup(
                reactions = postUiModel.reactions,
                myReaction = postUiModel.myReaction,
            )
        }

        EventRow(
            isFollowed = postUiModel.isFollowing,
            galleryVisible = galleryVisible,
            activatedReaction = postUiModel.myReaction,
            onReactionClick = { onReactionClick(postUiModel.postId, it) },
            onGalleryClick = { onGalleryClick(postUiModel.authorId) },
            onFollowClick = { onFollowClick(postUiModel) }
        )

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun EventRow(
    isFollowed: Boolean = false,
    galleryVisible: Boolean = true,
    activatedReaction: Reaction? = null,
    onReactionClick: (Reaction) -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onFollowClick: () -> Unit = {}
) {
    var isExpand by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { isExpand = true }) {
            Icon(
                imageVector = activatedReaction?.getIcon()
                    ?: ImageVector.vectorResource(com.kolown.porring.core.designsystem.R.drawable.ic_reaction_none),
                tint = if (activatedReaction != null) Primary else PrimaryDark,
                contentDescription = stringResource(com.kolown.porring.core.ui.R.string.string_reaction_button),
                modifier = Modifier
                    .size(30.dp)
            )
        }
        DropdownMenu(
            modifier = Modifier
                .wrapContentWidth(),
            expanded = isExpand,
            shadowElevation = 0.dp,
            containerColor = Color.Transparent,
            onDismissRequest = { isExpand = false }
        ) {
            ReactionSelector(
                selectedReaction = activatedReaction,
                onReactionClick = onReactionClick,
                onDismiss = { isExpand = false }
            )
        }

        Spacer(Modifier.weight(1f))

        if (galleryVisible) {
            IconButton(
                onClick = onGalleryClick
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(com.kolown.porring.core.ui.R.drawable.ic_gallery),
                    contentDescription = stringResource(com.kolown.porring.core.ui.R.string.string_gallery),
                    tint = PrimaryDark
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        IconButton(
            onClick = onFollowClick,
        ) {
            if (isFollowed) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    imageVector = ImageVector.vectorResource(com.kolown.porring.core.ui.R.drawable.ic_follow_bg),
                    contentDescription = null,
                )
            }
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(com.kolown.porring.core.ui.R.drawable.ic_follow),
                contentDescription = stringResource(com.kolown.porring.core.ui.R.string.string_follow),
                tint = if (isFollowed) Background else PrimaryDark,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
private fun DetailScreenPreview() {
    DetailContent()
}