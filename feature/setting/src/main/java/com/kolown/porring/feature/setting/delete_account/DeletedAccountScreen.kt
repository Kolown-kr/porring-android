package com.kolown.porring.feature.setting.delete_account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTextField
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.Error
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.designsystem.ui.theme.SurfaceError
import com.kolown.porring.feature.setting.R

@Composable
internal fun DeletedAccountRoute(
    viewModel: DeletedAccountViewModel = hiltViewModel(),
    navigateToHome: () -> Unit = {},
    popBackStack: () -> Unit = {},
    padding: PaddingValues
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        viewModel.isDeleteAccount.collect {
            if (it) {
                popBackStack()
            }
        }
    }

    DeletedAccountScreen(
        state = state,
        onPasswordChange = viewModel::onPasswordChange,
        onDeletedClick = viewModel::deleteAccount,
        popBackStack = popBackStack,
        padding = padding
    )
}

@Composable
private fun DeletedAccountScreen(
    state: DeletedAccountViewModel.State = DeletedAccountViewModel.State(),
    onPasswordChange: (String) -> Unit = {},
    onDeletedClick: () -> Unit = {},
    popBackStack: () -> Unit = {},
    padding: PaddingValues = PaddingValues()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        PorringTopAppBar(
            title = "회원탈퇴",
            navigationIcon = {
                PorringIconButton(
                    icon = ImageVector.vectorResource(com.kolown.porring.core.designsystem.R.drawable.ic_arrow_back),
                    onClick = popBackStack,
                    contentDescription = stringResource(R.string.string_back_button)
                )
            }
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            PorringTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                hint = "비밀번호를 입력하세요",
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = onDeletedClick,
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = state.isEnableButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Error,
                    disabledContainerColor = SurfaceError,
                )
            ) {
                Text(
                    text = "탈퇴하기",
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    PorringTheme(true) {
        DeletedAccountScreen()
    }
}