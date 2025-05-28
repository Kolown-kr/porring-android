package com.kolown.porring.feature.detail

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.Background
import com.kolown.porring.core.designsystem.ui.theme.BackgroundDark
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.PrimaryDark
import com.kolown.porring.core.model.PageState
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.ui.component.BetaPorringAlertDialog
import com.kolown.porring.core.ui.component.CoilImage
import com.kolown.porring.core.ui.component.FollowDialog
import com.kolown.porring.core.ui.component.LocalSnackBarBridge
import com.kolown.porring.core.ui.component.ReactionDialog
import com.kolown.porring.core.ui.component.ReactionGroup
import com.kolown.porring.core.ui.component.toImage
import com.kolown.porring.feature.detail.component.FullScreenEffect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
internal fun DetailRoute(
    type: MainMenuRoute.Detail.Type,
    order: Int,
    postId: String?,
    padding: PaddingValues = PaddingValues(),
    viewModel: DetailViewModel = hiltViewModel(),
    navigateToTheir: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
) {
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val pagerState = rememberPagerState { posts.itemCount }

    var followPost by remember { mutableStateOf<PostContentModel?>(null) }
    var reelsModePostUrl by remember { mutableStateOf<String?>(null) }
    val snackBarBridge = LocalSnackBarBridge.current

    val imageRatioMap = remember { mutableStateMapOf<Int, Float>() }
    var currentImageRatio by remember { mutableFloatStateOf(4f / 5f) }

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
            followPost = post
        }
    }

    followPost?.let { post ->
        if (post.isFollower) {
            BetaPorringAlertDialog(
                title = "팔로우 취소",
                description = "팔로우를 취소하시겠습니까?",
                dismissText = "취소",
                confirmText = "확인",
                onConfirm = { viewModel.cancelFollow(post.authorId) },
                onDismissRequest = { followPost = null }
            )
        } else {
            FollowDialog(
                onClickConfirm = { viewModel.registerFollow(post.authorId, it) },
                onClickCancel = { followPost = null }
            )
        }
    }

    if (reelsModePostUrl != null) {
        FullScreenEffect()
        BackHandler(onBack = { reelsModePostUrl = null })
        ReelsScreen(
            postUrl = reelsModePostUrl ?: "",
            imageRatio = currentImageRatio,
            onDismiss = { reelsModePostUrl = null }
        )
    } else {
        DetailScreen(
            posts = posts,
            pagerState = pagerState,
            padding = padding,
            imageRatioMap = imageRatioMap,
            eventRowVisible = type != MainMenuRoute.Detail.Type.MY,
            galleryVisible = type == MainMenuRoute.Detail.Type.DEFAULT,
            onUpdateRatio = { page, newRatio ->
                imageRatioMap[page] = newRatio
                currentImageRatio = newRatio
            },
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
    posts: LazyPagingItems<PostContentModel>,
    pagerState: PagerState = rememberPagerState(initialPage = 0) { posts.itemCount },
    padding: PaddingValues = PaddingValues(),
    imageRatioMap: Map<Int, Float> = mapOf(),
    eventRowVisible: Boolean = true,
    galleryVisible: Boolean = true,
    onUpdateRatio: (Int, Float) -> Unit = { _, _ -> },
    onShowReelsMode: (String) -> Unit = {},
    onReactionClick: (String, Reactions) -> Unit = { _, _ -> },
    onGalleryClick: (String) -> Unit = {},
    onFollowClick: (PostContentModel) -> Unit = {},
    popBackStack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(padding)
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
                post = post,
                eventRowVisible = eventRowVisible,
                galleryVisible = galleryVisible,
                imageRatio = imageRatioMap[page] ?: (4f / 5f),
                onUpdateRatio = { onUpdateRatio(page, it) },
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
    post: PostContentModel = PostContentModel.EMPTY,
    eventRowVisible: Boolean = true,
    galleryVisible: Boolean = true,
    imageRatio: Float = 4f / 5f,
    onUpdateRatio: (Float) -> Unit = {},
    onShowReelsMode: (String) -> Unit = {},
    onReactionClick: (String, Reactions) -> Unit = { _, _ -> },
    onGalleryClick: (String) -> Unit = {},
    onFollowClick: (PostContentModel) -> Unit = {}
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
                imageUrl = post.imageUrl,
                imageRatio = imageRatio,
                onClick = { onShowReelsMode(post.imageUrl) },
                updateImageRatio = onUpdateRatio
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
                    text = post.description,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                )

                if (post.tags.isNotEmpty()) {
                    Text(
                        text = buildString {
                            post.tags.forEachIndexed { index, tag ->
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
                reactions = post.reactions,
                myReaction = post.myReaction,
            )
        }

        if (eventRowVisible) {
            EventRow(
                isFollowed = post.isFollower,
                galleryVisible = galleryVisible,
                activatedReaction = post.myReaction,
                onReactionClick = { onReactionClick(post.postId, it) },
                onGalleryClick = { onGalleryClick(post.authorId) },
                onFollowClick = { onFollowClick(post) }
            )
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun EventRow(
    isFollowed: Boolean = false,
    galleryVisible: Boolean = true,
    activatedReaction: Reactions? = null,
    onReactionClick: (Reactions) -> Unit = {},
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

@Composable
private fun ReelsScreen(
    postUrl: String = "",
    imageRatio: Float = 4f / 5f,
    onDismiss: () -> Unit = {},
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    var imageSize by remember { mutableStateOf(IntSize.Zero) }

    val minScale = 1f
    val maxScale = 2f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .onSizeChanged { boxSize = it }
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(minScale, maxScale)

                        val scaledImageWidth = imageSize.width * scale
                        val scaledImageHeight = imageSize.height * scale

                        val maxOffsetX = ((scaledImageWidth - boxSize.width) / 2f).coerceAtLeast(0f)
                        val maxOffsetY =
                            ((scaledImageHeight - boxSize.height) / 2f).coerceAtLeast(0f)

                        offset = Offset(
                            x = (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                            y = (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                        )
                    }
                }
        ) {
            CoilImage(
                modifier = Modifier
                    .align(Alignment.Center)
                    .onSizeChanged { imageSize = it }
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    ),
                imageUrl = postUrl,
                imageRatio = imageRatio,
                isRipple = false
            )
        }

        PorringTopAppBar(
            trailingIcon = {
                PorringIconButton(
                    icon = Icons.Default.Close,
                    onClick = onDismiss,
                    contentDescription = stringResource(R.string.string_end_mode),
                    color = Color.White
                )
            }
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
private fun DetailScreenPreview() {
    DetailContent()
}

@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
private fun ReelsScreenPreview() {
    ReelsScreen()
}