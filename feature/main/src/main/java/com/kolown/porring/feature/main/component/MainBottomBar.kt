package com.kolown.porring.feature.main.component

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.kolown.porring.core.common.component.LocalSnackBarBridge
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.main.R
import com.kolown.porring.feature.main.navigation.MainMenu
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun MainBottomBar(
    isLoggedIn: Boolean = false,
    visible: Boolean,
    menus: PersistentList<MainMenu>,
    currentMenu: MainMenu?,
    onMenuSelected: (MainMenu) -> Unit,
) {
    val activity = LocalView.current.context as Activity
    val snackBarBridge = LocalSnackBarBridge.current

    if (visible) {
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
                isGranted && isLoggedIn -> onMenuSelected(MainMenu.CAMERA)
                isGranted && isLoggedIn.not() -> snackBarBridge.postSnackBarEvent(SnackBarEvent.LoginRequired())
                shouldShowRationale -> showRationale = true
                else -> showSetting = true
            }
        }

        CurvedBottomBar(
            menuItems = menus,
            currentItem = currentMenu ?: MainMenu.HOME,
        ) { menu ->
            if (menu.route == MainMenuRoute.Camera) {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                onMenuSelected(menu)
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
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMainBottomBar() {
    MainBottomBar(
        visible = true,
        menus = MainMenu.entries.toPersistentList(),
        currentMenu = MainMenu.HOME,
        onMenuSelected = {}
    )
}
