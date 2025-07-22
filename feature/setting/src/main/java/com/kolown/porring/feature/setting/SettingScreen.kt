package com.kolown.porring.feature.setting

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.kolown.porring.core.designsystem.R.drawable
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.feature.setting.component.TextMenu

@Composable
internal fun SettingRoute(
    popBackStack: () -> Unit,
    updateLoginState: () -> Unit,
    navigateToUserInfo: () -> Unit = {},
    settingViewModel: SettingViewModel = hiltViewModel(),
) {
    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(true) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            settingViewModel.logoutEnd.collect { logoutComplete ->
                if (logoutComplete) {
                    updateLoginState()
                    popBackStack()
                }
            }
        }
    }

    SettingScreen(
        clickLogout = settingViewModel::logout,
        navigateToUserInfo = navigateToUserInfo,
        popBackStack = popBackStack,
    )
}

@Composable
private fun SettingScreen(
    clickLogout: () -> Unit = {},
    navigateToUserInfo: () -> Unit = {},
    popBackStack: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        val context = LocalContext.current
        val showToast = { msg: String -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
        val showUpcomingToast = { showToast(context.getString(R.string.string_not_ready_feature)) }

        PorringTopAppBar(title = stringResource(R.string.string_settomg), navigationIcon = {
            PorringIconButton(
                icon = ImageVector.vectorResource(drawable.ic_arrow_back),
                onClick = popBackStack,
                contentDescription = stringResource(R.string.string_back_button)
            )
        }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Section(
                title = "사용자"
            ) {
                TextMenu(
                    title = stringResource(R.string.string_menu_show_my_reaction),
                    onClick = { showUpcomingToast() }
                )
            }

            Section(
                title = stringResource(R.string.string_menu_notify)
            ) {
                TextMenu(
                    title = "알림 설정",
                    onClick = { showUpcomingToast() }
                )
            }

            Section(
                title = "계정"
            ) {
                TextMenu(
                    title = stringResource(R.string.string_menu_user_info),
                    onClick = navigateToUserInfo
                )
                TextMenu(
                    title = stringResource(R.string.string_menu_logout),
                    color = Color.Red,
                    onClick = clickLogout
                )
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(),
            text = title,
            style = PorringTheme.typography.label,
            color = PorringTheme.colors.secondary
        )

        content()
    }
}

@Composable
private fun TextMenu(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(48.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color = PorringTheme.colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            style = PorringTheme.typography.body,
            text = title,
            color = color,
        )
    }
}

@Preview
@Composable
private fun Prev() {
    PorringTheme {
        SettingScreen()
    }
}