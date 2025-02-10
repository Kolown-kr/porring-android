package com.kolown.porring.feature.detail

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.core.ui.component.CoilImage
import com.kolown.porring.core.ui.component.ReactionGroup

@Composable
internal fun DetailRoute(
    type: MainMenuRoute.Detail.Type,
    order: Int,
    navigateToTheir: (String) -> Unit,
    popBackStack: () -> Unit,
    padding: PaddingValues = PaddingValues(),
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init(type, order)
    }

    DetailScreen(
        posts = posts,
        state = state,
        padding = padding,
    )
}

@Composable
private fun DetailScreen(
    posts: LazyPagingItems<PostContentModel>,
    state: DetailViewModel.State = DetailViewModel.State(),
    padding: PaddingValues = PaddingValues(),
    onShowReelsMode: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    popBackStack: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(padding)
            .clickable(onClick = onShowReelsMode)
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
                onFavoriteClick = onFavoriteClick,
                onGalleryClick = onGalleryClick,
                onFollowClick = onFollowClick
            )
        }
    }
}

@Composable
private fun DetailContent(
    post: PostContentModel = PostContentModel.EMPTY,
    onFavoriteClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onFollowClick: () -> Unit = {}
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
                modifier = Modifier.fillMaxWidth(),
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

            ReactionGroup(reactions = listOf(Reactions.LOVE, Reactions.STAR))
        }

        if (true) {
            EventRow(
                isFavorite = post.myReaction != null,
                isFollowed = post.isFollower,
                onFavoriteClick = onFavoriteClick,
                onGalleryClick = onGalleryClick,
                onFollowClick = onFollowClick
            )
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun EventRow(
    isFavorite: Boolean = false,
    isFollowed: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onGalleryClick: () -> Unit = {},
    onFollowClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                .clickable(onClick = onFavoriteClick)
        )

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


@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
private fun DetailScreenPreview() {
    DetailContent()
}
//@Composable
//private fun DetailScreen(
//    firstItem: PostContentModel = PostContentModel("", "", "", "", "", emptyList(), false, emptyList()),
//    pagingItems: LazyPagingItems<PostContentModel>,
//    pagerState: PagerState,
//    isLoggedIn: Boolean = false,
//    onChangeReelsMode: (Boolean) -> Unit = {},
//    popBackStack: () -> Unit = {},
//    updateMainPostReaction: (PostContentModel, Reactions) -> Unit = { _, _ -> },
//    onSelectReaction: (PostContentModel, Reactions) -> Unit = { _, _ -> },
//    isReelsMode: Boolean = true,
//    isPopBackStack: Boolean = false,
//    padding: PaddingValues = PaddingValues(),
//    navigateToTheir: (String) -> Unit = {},
//    updatePage: (Int) -> Unit = {},
//    onFollowClick: (String, String) -> Unit = { _, _ -> },
//    onUnfollowClick: (String) -> Unit = {},
//    followerState: State<Pair<String, Boolean>?> = mutableStateOf(null),
//    updateFollow: (String) -> Unit = {}
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(BackgroundDark)
//            .padding(padding)
//    ) {
//        DetailContent(
//            isLoggedIn = isLoggedIn,
//            isReelsMode = isReelsMode,
//            onChangeReelsMode = onChangeReelsMode,
//            updateMainPostReaction = updateMainPostReaction,
//            onSelectReaction = onSelectReaction,
//            pagingItems = pagingItems,
//            pagerState = pagerState,
//            isPopBackStack = isPopBackStack,
//            firstItem = firstItem,
//            navigateToTheir = navigateToTheir,
//            updatePage = updatePage,
//            onFollowClick = onFollowClick,
//            onUnfollowClick = onUnfollowClick,
//            followerState = followerState,
//            updateFollow = updateFollow
//        )
//
//        PorringTopAppBar(
//            navigationIcon = {
//                if (isReelsMode) {
//                    PorringIconButton(
//                        icon = ImageVector.vectorResource(drawable.ic_arrow_back),
//                        onClick = popBackStack,
//                        contentDescription = stringResource(R.string.string_go_back),
//                        color = Color.White
//                    )
//                }
//            },
//            trailingIcon = {
//                if (isReelsMode.not()) {
//                    PorringIconButton(
//                        icon = Icons.Default.Close,
//                        onClick = {
//                            onChangeReelsMode(true)
//                        },
//                        contentDescription = stringResource(R.string.string_end_mode),
//                        color = Color.White
//                    )
//                }
//            }
//        )
//    }
//}
//
//
//@Composable
//private fun DetailContent(
//    isLoggedIn: Boolean,
//    onChangeReelsMode: (Boolean) -> Unit,
//    updateMainPostReaction: (PostContentModel, Reactions) -> Unit,
//    onSelectReaction: (PostContentModel, Reactions) -> Unit,
//    isReelsMode: Boolean,
//    pagingItems: LazyPagingItems<PostContentModel>,
//    isPopBackStack: Boolean = false,
//    pagerState: PagerState,
//    firstItem: PostContentModel,
//    navigateToTheir: (String) -> Unit,
//    updatePage: (Int) -> Unit,
//    onFollowClick: (String, String) -> Unit = { _, _ -> },
//    onUnfollowClick: (String) -> Unit = {},
//    followerState: State<Pair<String, Boolean>?>,
//    updateFollow: (String) -> Unit = {}
//) {
//
//    VerticalPager(
//        modifier = Modifier.fillMaxSize(),
//        state = pagerState,
//        userScrollEnabled = isReelsMode,
//        beyondViewportPageCount = 3
//    ) { page ->
//
//        val imageItem = when (page) {
//            0 -> firstItem
//            pagingItems.itemCount + 1 -> null
//            else -> {
//                pagingItems[page - 1] ?: return@VerticalPager
//            }
//        }
//
//        DetailItem(
//            isLoggedIn = isLoggedIn,
//            isReelsMode = isReelsMode,
//            onChangeReelsMode = onChangeReelsMode,
//            updateMainPostReaction = updateMainPostReaction,
//            onSelectReaction = onSelectReaction,
//            imageItem = imageItem,
//            isPopBackStack = isPopBackStack,
//            navigateToTheir = navigateToTheir,
//            updatePage = {
//                updatePage(pagerState.currentPage)
//            },
//            onFollowClick = onFollowClick,
//            onUnfollowClick = onUnfollowClick,
//            followerState = followerState,
//            updateFollow = updateFollow
//        )
//    }
//
//}

