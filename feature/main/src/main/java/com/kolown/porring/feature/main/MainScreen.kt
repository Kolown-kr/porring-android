package com.kolown.porring.feature.main

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kolown.porring.core.designsystem.ui.theme.PrimaryDark
import com.kolown.porring.core.designsystem.ui.theme.SnackBarContainer
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.ui.component.showSnackBarWithData
import com.kolown.porring.feature.main.component.MainBottomBar
import com.kolown.porring.feature.main.component.MainNavHost
import com.kolown.porring.feature.main.navigation.MainMenu
import com.kolown.porring.feature.main.navigation.MainNavigator
import com.kolown.porring.feature.main.navigation.rememberMainNavigator
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
    mainViewModel: MainViewModel = hiltViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val isLoggedIn by mainViewModel.loginState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        mainViewModel.snackBarFlow.collect {
            when (it) {
                is SnackBarEvent.LoginRequired -> {
                    snackBarHostState.showSnackBarWithData(it).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            if (!isLoggedIn) navigator.navigateToLogin()
                        }
                    }
                }

                is SnackBarEvent.Message -> {
                    snackBarHostState.showSnackBarWithData(it)
                }
            }
        }
    }

    MainScreenContent(
        navigator = navigator,
        snackBarHostState = snackBarHostState,
    )
}

@Composable
private fun MainScreenContent(
    navigator: MainNavigator,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { CustomSnackBar(snackBarHostState) },
        content = { padding ->
            MainNavHost(
                navigator = navigator,
                padding = padding,
            )
        },
        bottomBar = {
            MainBottomBar(
                visible = navigator.isShowBottomBar(),
                menus = MainMenu.entries.toPersistentList(),
                currentMenu = navigator.currentMenu,
                onMenuSelected = { navigator.navigate(it) }
            )
        }
    )
}

@Composable
private fun CustomSnackBar(
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    SnackbarHost(hostState = snackBarHostState) { snackBarData ->
        Snackbar(
            snackbarData = snackBarData,
            containerColor = SnackBarContainer,
            contentColor = PrimaryDark,
            actionContentColor = Color.Green
        )
    }
}

@Preview
@Composable
private fun CustomSnackBarPreview() {
    CustomSnackBar()
}
