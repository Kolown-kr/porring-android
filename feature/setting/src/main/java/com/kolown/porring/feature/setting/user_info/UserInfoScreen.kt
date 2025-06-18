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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kolown.porring.core.designsystem.R
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.Background
import com.kolown.porring.core.designsystem.ui.theme.ErrorContainer
import com.kolown.porring.core.designsystem.ui.theme.OnBackground
import com.kolown.porring.core.designsystem.ui.theme.OnErrorContainer
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.Secondary

@Composable
internal fun UserInfoRoute(
    viewModel: UserInfoViewModel = hiltViewModel(),
    popBackStack: () -> Unit = {},
    padding: PaddingValues = PaddingValues(),
) {

    UserInfoScreen(

    )
}

@Composable
private fun UserInfoScreen(
    popBackStack: () -> Unit = {},
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
                description = "user1234@example.com"
            )

            Section(
                title = "사용자 이메일",
                description = "user1234@example.com"
            ) {
                Text(
                    text = "변경하기",
                    style = MaterialTheme.typography.labelMedium,
                    color = Primary
                )
            }

            Section(
                title = "사용자 통계",
                description = "2000년 00월 00일 가입"
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
                    containerColor = ErrorContainer,
                    contentColor = OnErrorContainer,
                ),
                onClick = {}
            ) {
                Text(
                    text = "탈퇴하기",
                    style = MaterialTheme.typography.bodyMedium
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
            style = MaterialTheme.typography.labelMedium,
            color = Secondary
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
                style = MaterialTheme.typography.bodyMedium,
                color = OnBackground,
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
            style = MaterialTheme.typography.labelMedium,
            color = Secondary
        )
        Text(
            modifier = Modifier
                .padding(vertical = 8.dp),
            text = value.toString() + unit,
            style = MaterialTheme.typography.titleMedium,
            color = Primary
        )
    }
}

@Preview
@Composable
private fun Preview() {
    PorringTheme(true) {
        UserInfoScreen()
    }
}