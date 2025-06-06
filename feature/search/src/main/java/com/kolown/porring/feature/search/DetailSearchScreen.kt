package com.kolown.porring.feature.search


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kolown.porring.core.designsystem.ui.theme.PrimaryContainerDark
import com.kolown.porring.core.model.Reaction
import com.kolown.porring.core.ui.component.DetailItem
import com.kolown.porring.core.ui.component.DetailTopAppBar
import com.kolown.porring.core.ui.model.PostUiModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun DetailSearchRoute(
    padding: PaddingValues = PaddingValues(),
    popBackStack: () -> Unit,
    navigateToTheir: (String) -> Unit,
    isLoggedIn: Boolean = false,
    viewModel: DetailSearchViewModel = hiltViewModel(),
    imageViewModel: SearchImageViewModel = hiltViewModel()
) {
    val images = imageViewModel.imagesFlow.collectAsLazyPagingItems()
    var isReelsMode by remember { mutableStateOf(true) }
    val pagerState = rememberPagerState(initialPage = viewModel.firstPage) { images.itemCount }
    val followState = viewModel.followState.collectAsStateWithLifecycle(null)
    var blockDoubleTab by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    DetailSearchScreen(
        padding = padding,
        pagingItems = images,
        popBackStack = popBackStack,
        isReelsMode = isReelsMode,
        onChangeReelsMode = { isReelsMode = it },
        navigateToTheir = { id ->
            coroutineScope.launch {
                blockDoubleTab = true
                navigateToTheir(id)
                delay(300)
                blockDoubleTab = false
            }
        },
        blockDoubleTab = blockDoubleTab,
        pagerState = pagerState,
        followerState = followState,
        onFollowClick = viewModel::followUser,
        onUnfollowClick = viewModel::unFollowUser,
        onSelectReaction = viewModel::selectReaction,
        checkPostIsMine = viewModel::checkPostIsMine,
        isLoggedIn = isLoggedIn,
    )
    BackHandler(enabled = true) {
        blockDoubleTab = true
        popBackStack()
    }
}

@Composable
fun DetailSearchScreen(
    isLoggedIn: Boolean,
    pagerState: PagerState,
    popBackStack: () -> Unit = {},
    onSelectReaction: (PostUiModel, Reaction) -> Unit = { _, _ -> },
    isReelsMode: Boolean = true,
    pagingItems: LazyPagingItems<PostUiModel>,
    padding: PaddingValues = PaddingValues(),
    navigateToTheir: (String) -> Unit = {},
    updatePage: (Int) -> Unit = {},
    blockDoubleTab: Boolean = false,
    onFollowClick: (String, String) -> Unit = { _, _ -> },
    onUnfollowClick: (String) -> Unit = {},
    onChangeReelsMode: (Boolean) -> Unit = {},
    followerState: State<Pair<String, Boolean>?>,
    checkPostIsMine: (String) -> Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryContainerDark)
            .padding(padding)
    ) {

        DetailContent(
            onSelectReaction = onSelectReaction,
            isReelsMode = isReelsMode,
            onChangeReelsMode = onChangeReelsMode,
            pagingItems = pagingItems,
            pagerState = pagerState,
            blockDoubleTab = blockDoubleTab,
            navigateToTheir = navigateToTheir,
            updatePage = updatePage,
            onFollowClick = onFollowClick,
            onUnfollowClick = onUnfollowClick,
            followerState = followerState,
            checkPostIsMine = checkPostIsMine,
            isLoggedIn = isLoggedIn,
        )

        DetailTopAppBar(
            isReelsMode = isReelsMode,
            onChangeReelsMode = onChangeReelsMode,
            popBackStack = popBackStack
        )
    }
}


@Composable
fun DetailContent(
    onSelectReaction: (PostUiModel, Reaction) -> Unit,
    isReelsMode: Boolean,
    onChangeReelsMode: (Boolean) -> Unit,
    pagingItems: LazyPagingItems<PostUiModel>,
    pagerState: PagerState,
    blockDoubleTab: Boolean = false,
    navigateToTheir: (String) -> Unit,
    updatePage: (Int) -> Unit,
    onFollowClick: (String, String) -> Unit = { _, _ -> },
    onUnfollowClick: (String) -> Unit = {},
    followerState: State<Pair<String, Boolean>?> = mutableStateOf(null),
    isLoggedIn: Boolean,
    checkPostIsMine: (String) -> Boolean = { _ -> false }
) {

    VerticalPager(
        modifier = Modifier.fillMaxSize(),
        state = pagerState,
        userScrollEnabled = isReelsMode,
    ) { page ->

        val imageItem = pagingItems[page] ?: return@VerticalPager

        DetailItem(
            isLoggedIn = isLoggedIn,
            isReelsMode = isReelsMode,
            onChangeReelsMode = onChangeReelsMode,
            isPopBackStack = blockDoubleTab,
            onSelectReaction = onSelectReaction,
            imageItem = imageItem,
            navigateToTheir = navigateToTheir,
            updatePage = {
                updatePage(pagerState.currentPage)
            },
            onFollowClick = onFollowClick,
            onUnfollowClick = onUnfollowClick,
            followerState = followerState,
            checkPostIsMine = checkPostIsMine,
        )

    }
}