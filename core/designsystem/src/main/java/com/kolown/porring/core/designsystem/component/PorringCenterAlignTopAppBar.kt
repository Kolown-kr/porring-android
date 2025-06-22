package com.kolown.porring.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme

@Composable
fun PorringCenterAlignTopAppBar(
    title: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        if (navigationIcon == null) {
            Spacer(Modifier.size(48.dp))
        } else {
            navigationIcon()
        }


        title?.let {
            Text(
                style = PorringTheme.typography.headline,
                color = PorringTheme.colors.onBackground,
                text = title,
                modifier = Modifier
            )
        }

        if (trailingIcon == null) {
            Spacer(Modifier.size(48.dp))
        } else {
            trailingIcon()
        }
    }
}