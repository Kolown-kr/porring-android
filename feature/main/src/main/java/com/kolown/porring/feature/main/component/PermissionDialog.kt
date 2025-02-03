package com.kolown.porring.feature.main.component

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.Error
import com.kolown.porring.feature.main.R

@Composable
fun PermissionRationaleDialog(
    permission: String = "",
    onDismissRequest: () -> Unit = {},
    permissionLauncher: ManagedActivityResultLauncher<String, Boolean> = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        Card(
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(color = Color.White)
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_camera_24dp),
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.camera_permission_guide),
                    color = Primary
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    text = stringResource(R.string.camera_rationale_script)
                )

                TextButton(
                    onClick = {
                        onDismissRequest()
                        permissionLauncher.launch(permission)
                    }
                ) {
                    Text(
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        text = stringResource(R.string.close)
                    )
                }
            }
        }
    }
}

@Composable
fun ShowSettingDialog(
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        Card(
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(color = Color.White)
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_camera_24dp),
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.camera_permission_guide),
                    color = Primary
                )
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                    text = stringResource(R.string.camera_permission_guide_script)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.End)
                ) {
                    TextButton(
                        onClick = {
                            onDismissRequest()
                        }
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelLarge,
                            color = Error,
                            text = stringResource(R.string.close)
                        )
                    }

                    TextButton(
                        onClick = {
                            onDismissRequest()
                            onConfirm()
                        }
                    ) {
                        Text(
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            text = stringResource(R.string.go_to_setting)
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_5
)
@Composable
fun PreviewPermissionEduDialog() {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            PermissionRationaleDialog()
        }
    }
}

@Preview(
    showBackground = true,
    device = Devices.PIXEL_5
)
@Composable
fun PreviewPermissionDeniedDialog() {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            ShowSettingDialog()
        }
    }
}