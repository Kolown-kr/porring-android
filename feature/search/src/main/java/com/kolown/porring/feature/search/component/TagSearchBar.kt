package com.kolown.porring.feature.search.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.icon.PorringIcons
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.Surface2
import com.kolown.porring.feature.search.R

@Composable
internal fun TagSearchBar(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isFocus: Boolean = false,
    onFocusChanged: (Boolean) -> Unit = {},
    onClearClick: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isFocus) {
        if (isFocus) {
            focusRequester.requestFocus()
        }
    }

    TextField(
        value = text,
        onValueChange = onValueChange,
        modifier = modifier
            .focusRequester(focusRequester)
            .fillMaxWidth()
            .clip(CircleShape)
            .clickable { onFocusChanged(!isFocus) },
        singleLine = true,
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Surface2,
            disabledContainerColor = Surface2,
            focusedContainerColor = Surface2,
        ),
        enabled = isFocus,
        textStyle = TextStyle(
            color = Primary,
            fontWeight = FontWeight.SemiBold
        ),
        leadingIcon = {
            IconButton(onClick = { onFocusChanged(false) }) {
                if (isFocus) Icon(
                    imageVector = PorringIcons.Default.ArrowBack,
                    modifier = Modifier.size(24.dp),
                    contentDescription = stringResource(R.string.string_search_back),
                    tint = Primary
                )
                else Icon(
                    imageVector = Icons.Default.Search,
                    tint = Primary,
                    contentDescription = stringResource(R.string.string_search)
                )
            }
        },
        trailingIcon = {
            if (isFocus) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.string_search_back),
                        tint = Primary
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    TagSearchBar(
        text = "",
        onValueChange = {},
    )
}