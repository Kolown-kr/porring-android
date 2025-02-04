package com.kolown.porring.feature.main.component

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.kolown.porring.core.common.component.LocalSnackBarBridge
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.PrimaryUnActive
import com.kolown.porring.core.model.SnackBarEvent
import com.kolown.porring.core.navigation.MainMenuRoute
import com.kolown.porring.feature.main.R
import com.kolown.porring.feature.main.navigation.MainMenu
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun MainBottomBar(
    isLoggedIn: Boolean = false,
    modifier: Modifier = Modifier,
    visible: Boolean,
    menus: PersistentList<MainMenu>,
    currentMenu: MainMenu?,
    onMenuSelected: (MainMenu) -> Unit,
) {
    val activity = LocalView.current.context as Activity
    val snackBarBridge = LocalSnackBarBridge.current

    if (visible) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            menus.forEach { menu ->
                var showRationale by remember { mutableStateOf(false) }
                var showSetting by remember { mutableStateOf(false) }
                val cameraPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        onMenuSelected(menu)
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                activity,
                                Manifest.permission.CAMERA
                            )
                        ) {
                            showRationale = true
                        } else {
                            showSetting = true
                        }
                    }
                }

                MainBottomBarItem(
                    menu = menu,
                    selected = menu == currentMenu,
                    onClick = {
                        if (menu.route == MainMenuRoute.Camera) {
                            if (isLoggedIn) {
                                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            } else {
                                snackBarBridge.postSnackBarEvent(SnackBarEvent.LoginRequired())
                            }
                        } else {
                            onMenuSelected(menu)
                        }
                    }
                )

                if (showRationale) {
                    PorringAlertDialog(
                        title = stringResource(R.string.camera_permission_guide),
                        description = stringResource(R.string.camera_rationale_script),
                        iconResId = R.drawable.ic_camera_24dp,
                        dismissText = stringResource(R.string.close),
                        confirmText = stringResource(R.string.confirm),
                        onDismissRequest = { showRationale = false },
                        onConfirm = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                    )
                }

                if (showSetting) {
                    val context = LocalContext.current

                    PorringAlertDialog(
                        title = stringResource(R.string.camera_permission_guide),
                        description = stringResource(R.string.camera_permission_guide_script),
                        iconResId = R.drawable.ic_camera_24dp,
                        dismissText = stringResource(R.string.close),
                        confirmText = stringResource(R.string.go_to_setting),
                        onDismissRequest = { showSetting = false },
                        onConfirm = {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data =
                                        Uri.fromParts("package", context.packageName, null)
                                }

                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.MainBottomBarItem(
    modifier: Modifier = Modifier,
    menu: MainMenu,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .weight(1f)
            .fillMaxHeight()
            .selectable(
                selected = selected,
                indication = null,
                role = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(menu.iconResId),
            contentDescription = menu.contentDescription,
            tint = if (selected || menu == MainMenu.CAMERA) Primary else PrimaryUnActive,
            modifier = Modifier.size(
                if (menu == MainMenu.CAMERA) 48.dp else 24.dp
            )
        )

        if (menu != MainMenu.CAMERA) {
            Text(
                text = menu.contentDescription,
                color = if (selected) Primary else PrimaryUnActive,
                style = MaterialTheme.typography.labelMedium
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
