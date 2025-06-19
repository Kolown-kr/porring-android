package com.kolown.porring.feature.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kolown.porring.core.designsystem.ui.theme.PrimaryDark
import com.kolown.porring.core.designsystem.ui.theme.SnackBarContainer
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.ui.component.LocalSnackBarBridge
import com.kolown.porring.core.ui.component.showSnackBarWithData
import com.kolown.porring.feature.main.component.MainBottomBar
import com.kolown.porring.feature.main.component.MainNavHost
import com.kolown.porring.feature.main.component.PorringAlertDialog
import com.kolown.porring.feature.main.model.SnackBarNavigation
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
    val isLoggedIn by mainViewModel.loginState.collectAsStateWithLifecycle(false)
    val activity = LocalView.current.context as Activity
    val snackBarBridge = LocalSnackBarBridge.current
    var showRationale by remember { mutableStateOf(false) }
    var showSetting by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.CAMERA
        )

        when {
            isGranted && isLoggedIn -> navigator.navigate(MainMenu.CAMERA)
            isGranted -> snackBarBridge.postSnackBarEvent(SnackBarEvent.LoginRequired())
            shouldShowRationale -> showRationale = true
            else -> showSetting = true
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.snackBarFlow.collect {
            val result = snackBarHostState.showSnackBarWithData(it)
            when (it) {
                is SnackBarEvent.LoginRequired -> {
                    if (result == SnackbarResult.ActionPerformed && !isLoggedIn) {
                        navigator.navigateToLogin()
                    }
                }

                is SnackBarEvent.Message -> {
                    if (result == SnackbarResult.ActionPerformed) {
                        it.onAction?.invoke()
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.navigationRequest.collect { nav ->
            when (nav) {
                SnackBarNavigation.ToGallery -> navigator.navigate(MainMenu.MY)
                is SnackBarNavigation.ToUpload -> navigator.navigateToUpload(
                    "",
                    0f,
                    nav.uploadModel
                )
            }
        }
    }

    if (showRationale) {
        PorringAlertDialog(
            title = stringResource(R.string.string_camera_permission_guide),
            description = stringResource(R.string.string_camera_rationale_script),
            iconResId = R.drawable.ic_camera_24dp,
            dismissText = stringResource(R.string.string_close),
            confirmText = stringResource(R.string.string_confirm),
            onDismissRequest = { showRationale = false },
            onConfirm = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
        )
    }

    if (showSetting) {
        PorringAlertDialog(
            title = stringResource(R.string.string_camera_permission_guide),
            description = stringResource(R.string.string_camera_permission_guide_script),
            iconResId = R.drawable.ic_camera_24dp,
            dismissText = stringResource(R.string.string_close),
            confirmText = stringResource(R.string.string_go_to_setting),
            onDismissRequest = { showSetting = false },
            onConfirm = {
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data =
                            Uri.fromParts("package", activity.packageName, null)
                    }

                activity.startActivity(intent)
            }
        )
    }

    MainScreenContent(
        navigator = navigator,
        snackBarHostState = snackBarHostState,
        onMenuSelected = navigator::navigate,
        onCameraSelected = {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        },
    )
}

@Composable
private fun MainScreenContent(
    navigator: MainNavigator,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onMenuSelected: (MainMenu) -> Unit = {},
    onCameraSelected: () -> Unit = {},
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
                onMenuSelected = onMenuSelected,
                onCameraSelected = onCameraSelected,
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
