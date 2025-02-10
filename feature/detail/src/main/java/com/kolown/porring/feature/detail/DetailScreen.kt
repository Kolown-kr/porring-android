package com.kolown.porring.feature.detail

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.BackgroundDark
import com.kolown.porring.core.designsystem.ui.theme.Gray
import com.kolown.porring.core.designsystem.ui.theme.PrimaryContainerDark
import com.kolown.porring.core.designsystem.ui.theme.PrimaryDark
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.model.Reactions
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.ui.component.CoilImage
import com.kolown.porring.core.ui.component.FollowDialog
import com.kolown.porring.core.ui.component.LocalSnackBarBridge
import com.kolown.porring.core.ui.component.ReactionDialog
import com.kolown.porring.core.ui.component.ReactionGroup
import com.kolown.porring.feature.detail.component.FullScreenEffect

@Composable
internal fun DetailRoute(
    type: MainMenuRoute.Detail.Type,
    order: Int,
    padding: PaddingValues = PaddingValues(),
    viewModel: DetailViewModel = hiltViewModel(),
    navigateToTheir: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
) {
    val posts = viewModel.posts.collectAsLazyPagingItems()
    var followPost by remember { mutableStateOf<PostContentModel?>(null) }
    var reelsModePostUrl by remember { mutableStateOf<String?>(null) }
    val snackBarBridge = LocalSnackBarBridge.current

    LaunchedEffect(Unit) {
        viewModel.init(type, order)
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
        FollowDialog(
            onClickConfirm = { viewModel.registerFollow(post.authorId, it) },
            onClickCancel = { followPost = null }
        )
    }

    reelsModePostUrl?.let { postUrl ->
        FullScreenEffect()
        BackHandler(onBack = { reelsModePostUrl = null })
        ReelsScreen(
            postUrl = postUrl,
            onDismiss = { reelsModePostUrl = null }
        )
    }

    DetailScreen(
        posts = posts,
        padding = padding,
        onShowReelsMode = { reelsModePostUrl = it },
        onReactionClick = viewModel::onReactionClick,
        onGalleryClick = navigateToTheir,
        onFollowClick = viewModel::onFollowClick,
        popBackStack = popBackStack
    )
}

@Composable
private fun DetailScreen(
    posts: LazyPagingItems<PostContentModel>,
    padding: PaddingValues = PaddingValues(),
    onShowReelsMode: (String) -> Unit = {},
    onReactionClick: (String, Reactions) -> Unit = {_,_ ->},
    onGalleryClick: (String) -> Unit = {},
    onFollowClick: (PostContentModel) -> Unit = {},
    popBackStack: () -> Unit = {},
) {
    Box(
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

        VerticalPager(
            modifier = Modifier.fillMaxSize(),
            state = rememberPagerState { posts.itemCount },
            beyondViewportPageCount = 3
        ) { page: Int ->
            val post = posts[page] ?: return@VerticalPager

            DetailContent(
                post = post,
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
    onShowReelsMode: (String) -> Unit = {},
    onReactionClick: (String, Reactions) -> Unit = {_,_ ->},
    onGalleryClick: (String) -> Unit = {},
    onFollowClick: (PostContentModel) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4 / 5f)
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { onShowReelsMode(post.imageUrl) },
                imageUrl = post.imageUrl,
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
                    color = Color.White
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

            ReactionGroup(reactions = post.reactions)
        }

        if (true) {
            EventRow(
                isFavorite = post.myReaction != null,
                isFollowed = post.isFollower,
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
    isFavorite: Boolean = false,
    isFollowed: Boolean = false,
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
                imageVector = if (isFavorite) {
                    Icons.Default.Favorite
                } else {
                    Icons.Outlined.FavoriteBorder
                },
                tint = PrimaryDark,
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
            ReactionDialog (
                activatedReaction = activatedReaction,
                selectedReaction = onReactionClick,
                onDismiss = { isExpand = false }
            )
        }

        Spacer(Modifier.weight(1f))


        DetailButton(
            onClick = onGalleryClick,
            imageVector = ImageVector.vectorResource(com.kolown.porring.core.ui.R.drawable.ic_detail_gallary),
            buttonText = stringResource(com.kolown.porring.core.ui.R.string.string_gallery)
        )

        Spacer(Modifier.width(12.dp))

        DetailButton(
            onClick = onFollowClick,
            imageVector = ImageVector.vectorResource(com.kolown.porring.core.ui.R.drawable.ic_detail_follow),
            buttonText = stringResource(com.kolown.porring.core.ui.R.string.string_follow),
            contentColor = if (isFollowed) PrimaryContainerDark else PrimaryDark,
            backgroundColor = if (isFollowed) PrimaryDark else PrimaryContainerDark
        )
    }
}

@Composable
private fun DetailButton(
    imageVector: ImageVector,
    buttonText: String,
    contentColor: Color = PrimaryDark,
    backgroundColor: Color = PrimaryContainerDark,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Icon(
            imageVector = imageVector, contentDescription = null, tint = contentColor
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = buttonText, color = contentColor)
    }
}

@Composable
private fun ReelsScreen(
    postUrl: String = "",
    onDismiss: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .zIndex(1f)
    ) {
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

        CoilImage(
            imageUrl = postUrl,
            modifier = Modifier.align(Alignment.Center),
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