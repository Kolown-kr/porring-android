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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.Background
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.PrimaryDark
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.Reaction
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.navigation.Route
import com.kolown.porring.core.ui.component.BetaPorringAlertDialog
import com.kolown.porring.core.ui.component.CoilImage
import com.kolown.porring.core.ui.component.FollowDialog
import com.kolown.porring.core.ui.component.ReactionDialog
import com.kolown.porring.core.ui.component.ReactionGroup
import com.kolown.porring.core.ui.component.toImage
import com.kolown.porring.core.ui.compositionlocal.LocalSnackBarBridge
import com.kolown.porring.core.ui.model.PostUiModel
import com.kolown.porring.feature.detail.component.FullScreenEffect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
internal fun DetailRoute(
    type: Route.Detail.Type,
    order: Int,
    authorId: String,
    postId: String?,
    viewModel: DetailViewModel = hiltViewModel(),
    navigateToTheir: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
) {
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val pagerState = rememberPagerState { posts.itemCount }

    var followPostUiModel by remember { mutableStateOf<PostUiModel?>(null) }
    var reelsModePostUrl by remember { mutableStateOf<String?>(null) }
    val snackBarBridge = LocalSnackBarBridge.current

    LaunchedEffect(Unit) {
        viewModel.initViewModel(
            type = type,
            postId = postId,
            authorId = authorId
        )
    }

    LaunchedEffect(Unit) {
        viewModel.initViewModel(
            type = type,
            postId = postId,
            authorId = authorId
        )
    }

    LaunchedEffect(Unit) {
        snapshotFlow { posts.itemSnapshotList.items }
            .filter { it.isNotEmpty() }
            .first()
            .let { items ->
                val index = items.indexOfFirst { it.postId == postId }
                pagerState.scrollToPage(if (index >= 0) index else order)
            }
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
            posts = posts,
            pagerState = pagerState,
            eventRowVisible = type != Route.Detail.Type.MY,
            galleryVisible = type == Route.Detail.Type.DEFAULT,
            onShowReelsMode = { reelsModePostUrl = it },
            onReactionClick = viewModel::onReactionClick,
            onGalleryClick = navigateToTheir,
            onFollowClick = viewModel::onFollowClick,
            popBackStack = popBackStack
        )
    }
}

@Composable
private fun DetailScreen(
    posts: LazyPagingItems<PostUiModel>,
    pagerState: PagerState = rememberPagerState(initialPage = 0) { posts.itemCount },
    eventRowVisible: Boolean = true,
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
                    icon = Icons.AutoMirrored.Default.KeyboardArrowLeft,
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
                val item = posts[index]
                "${item?.postId}"
            },
            beyondViewportPageCount = 3,
        ) { page: Int ->
            val post = posts[page] ?: return@HorizontalPager

            DetailContent(
                postUiModel = post,
                eventRowVisible = eventRowVisible,
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
    eventRowVisible: Boolean = true,
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

        if (eventRowVisible) {
            EventRow(
                isFollowed = postUiModel.isFollowing,
                galleryVisible = galleryVisible,
                activatedReaction = postUiModel.myReaction,
                onReactionClick = { onReactionClick(postUiModel.postId, it) },
                onGalleryClick = { onGalleryClick(postUiModel.authorId) },
                onFollowClick = { onFollowClick(postUiModel) }
            )
        }

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
                imageVector = if (activatedReaction != null) {
                    ImageVector.vectorResource(activatedReaction.toImage())
                } else {
                    ImageVector.vectorResource(com.kolown.porring.core.ui.R.drawable.ic_reaction_unselected)
                },
                tint = if (activatedReaction != null) Primary else PrimaryDark,
                contentDescription = stringResource(com.kolown.porring.core.ui.R.string.string_reaction_button),
                modifier = Modifier
                    .size(30.dp)
            )
        }
        DropdownMenu(
            modifier = Modifier
                .width(352.dp),
            expanded = isExpand,
            containerColor = Color.Transparent,
            shape = CircleShape,
            onDismissRequest = { isExpand = false }
        ) {
            ReactionDialog(
                activatedReaction = activatedReaction,
                selectedReaction = onReactionClick,
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