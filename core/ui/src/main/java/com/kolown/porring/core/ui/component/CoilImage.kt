package com.kolown.porring.core.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kolown.porring.core.ui.R
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.PrimaryUnActive
import com.kolown.porring.core.designsystem.ui.theme.Surface2
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CoilImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    isTextExist: Boolean = true,
    isClickedEnabled: Boolean = true,
    isRipple: Boolean = true,
    imageRatio: Float = 4f / 5f,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onDoubleClick: () -> Unit = {},
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val loadingModifier = modifier.shimmerEffect()
    val interactionSource = remember { MutableInteractionSource() }

    if (isError) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(color = Surface2),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                modifier = Modifier.fillMaxSize(1 / 2f),
                painter = painterResource(R.drawable.icon_lost_image),
                tint = PrimaryUnActive,
                contentDescription = "no_image"
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isTextExist) {
                Text(
                    text = stringResource(R.string.string_can_not_load),
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary
                )
            }
        }
    } else {
        Box(
            modifier = if (isLoading) loadingModifier else modifier,
        ) {
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(imageRatio)
                    .fillMaxWidth()
                    .then(
                        if (isClickedEnabled) Modifier.combinedClickable(
                            indication = if (isRipple) LocalIndication.current else null,
                            interactionSource = interactionSource,
                            onClick = onClick,
                            onLongClick = onLongClick,
                            onDoubleClick = onDoubleClick
                        ) else {
                            Modifier
                        }
                    ),
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                onLoading = {
                    isLoading = true
                    isError = false
                },
                onSuccess = {
                    coroutineScope.launch {
                        isLoading = false
                        isError = false
                    }
                },
                onError = {
                    isLoading = false
                    isError = true
                }
            )
        }
    }
}