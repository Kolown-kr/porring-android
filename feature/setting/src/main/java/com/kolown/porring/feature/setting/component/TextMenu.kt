package com.kolown.porring.feature.setting.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme

@Composable
internal fun TextMenu(
    title: String,
    color: Color = PorringTheme.colors.onBackground,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .size(48.dp)
        .clickable { onClick() }
        .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            style = PorringTheme.typography.body,
            text = title,
            color = color,
        )
    }
}