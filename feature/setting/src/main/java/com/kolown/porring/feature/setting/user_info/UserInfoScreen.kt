package com.kolown.porring.feature.setting.user_info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kolown.porring.core.designsystem.R
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTextField
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.Background
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.ui.ext.noRippleClickable
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun UserInfoRoute(
    viewModel: UserInfoViewModel = hiltViewModel(),
    popBackStack: () -> Unit = {},
    padding: PaddingValues = PaddingValues(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    if (state.isEmailDialogVisible) {
        ChangeDialog(
            title = "이메일 변경",
            description = "중요 알림과 계정 정보를 수신할" +
                    "이메일 주소를 입력해 주세요.",
            hint = "이메일 주소를 입력해 주세요.",
            textValue = state.dialogEmailAddress,
            onValueChange = viewModel::onEmailChange,
            onDismissRequest = viewModel::onEmailChangeDismiss,
            onConfirmClick = viewModel::requestEmailChange
        )
    }

    if (state.isDeleteDialogVisible) {
        ChangeDialog(
            title = "비밀번호 확인",
            description = "탈퇴를 위해 비밀번호를 확인해주세요.",
            hint = "비밀번호를 입력해 주세요.",
            textValue = state.dialogPassword,
            onValueChange = viewModel::onDeletePasswordChange,
            onDismissRequest = viewModel::onDeletePasswordDismiss,
            onConfirmClick = viewModel::requestDelete
        )
    }

    UserInfoScreen(
        state = state,
        popBackStack = popBackStack,
        onEmailChangeClick = viewModel::onEmailChangeClick,
        onDeleteClick = viewModel::onDeleteClick,
        padding = padding
    )
}

@Composable
private fun UserInfoScreen(
    state: UserInfoViewModel.State = UserInfoViewModel.State(),
    popBackStack: () -> Unit = {},
    onEmailChangeClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    padding: PaddingValues = PaddingValues(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
            .padding(padding)
    ) {
        PorringTopAppBar(
            title = "사용자 정보",
            navigationIcon = {
                PorringIconButton(
                    icon = ImageVector.vectorResource(R.drawable.ic_arrow_back),
                    onClick = popBackStack,
                    contentDescription = stringResource(com.kolown.porring.feature.setting.R.string.string_back_button)
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp,
                    vertical = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Section(
                title = "사용자 ID",
                description = state.user.email
            )

            Section(
                title = "사용자 이메일",
                description = state.user.receiverEmail
            ) {
                Text(
                    modifier = Modifier
                        .noRippleClickable(onEmailChangeClick),
                    text = "변경하기",
                    style = PorringTheme.typography.label,
                    color = PorringTheme.colors.primary
                )
            }

            Section(
                title = "사용자 통계",
                description = state.user.createAt.formatText()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatColumn(
                    label = "올린 사진",
                    value = 0,
                    unit = "개"
                )
                StatColumn(
                    label = "팔로우 수",
                    value = 0,
                    unit = "명"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(5.dp),
                contentPadding = PaddingValues(
                    horizontal = 20.dp,
                    vertical = 16.dp
                ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PorringTheme.colors.errorContainer,
                    contentColor = PorringTheme.colors.onErrorContainer,
                ),
                onClick = onDeleteClick
            ) {
                Text(
                    text = "탈퇴하기",
                    style = PorringTheme.typography.body
                )
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    description: String,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp),
            text = title,
            style = PorringTheme.typography.label,
            color = PorringTheme.colors.secondary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = description,
                style = PorringTheme.typography.body,
                color = PorringTheme.colors.onBackground,
            )

            trailingIcon?.invoke()
        }
    }
}

@Composable
private fun RowScope.StatColumn(
    label: String,
    value: Int,
    unit: String,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(
                horizontal = 12.dp,
            )
    ) {
        Text(
            modifier = Modifier
                .padding(vertical = 4.dp),
            text = label,
            style = PorringTheme.typography.label,
            color = PorringTheme.colors.secondary
        )
        Text(
            modifier = Modifier
                .padding(vertical = 8.dp),
            text = value.toString() + unit,
            style = PorringTheme.typography.title,
            color = PorringTheme.colors.primary
        )
    }
}

@Composable
private fun ChangeDialog(
    title: String,
    description: String,
    hint: String,
    textValue: String = "",
    onValueChange: (String) -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PorringTheme.colors.background)
                .padding(
                    top = 32.dp,
                    bottom = 20.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = PorringTheme.typography.headline,
                color = PorringTheme.colors.primary
            )

            Text(
                text = description,
                style = PorringTheme.typography.body,
                textAlign = TextAlign.Center,
                color = PorringTheme.colors.onSurface
            )

            Spacer(Modifier)

            PorringTextField(
                value = textValue,
                onValueChange = onValueChange,
                hint = hint
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = "취소",
                        style = PorringTheme.typography.label,
                        color = PorringTheme.colors.error
                    )
                }

                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = onConfirmClick
                ) {
                    Text(
                        text = "변경 하기",
                        style = PorringTheme.typography.label,
                        color = PorringTheme.colors.primary
                    )
                }
            }
        }
    }
}

private fun ZonedDateTime.formatText(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 '가입'", Locale.KOREAN)
    return format(formatter)
}

@Preview
@Composable
private fun Preview() {
    PorringTheme(true) {
        UserInfoScreen()
    }
}

@Preview
@Composable
private fun Preview2() {
    PorringTheme(true) {
        ChangeDialog(
            title = "이메일 변경",
            description = "중요 알림과 계정 정보를 수신할" +
                    "이메일 주소를 입력해 주세요.",
            hint = ""
        )
    }
}