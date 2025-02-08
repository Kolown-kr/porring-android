package com.kolown.porring.core.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kolown.porring.core.common.R
import com.kolown.porring.core.designsystem.ui.theme.Error
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.model.PostContentModel

@Composable
fun GalleryItem(
    postContentModel: PostContentModel,
    onLongClickImage: () -> Unit = {},
    onClickImage: () -> Unit = {},
    longClickEnabled: Boolean = true
) {
    val isDialogVisible = remember { mutableStateOf(false) }
    val imageRatio = remember { mutableFloatStateOf(4f / 5f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        CoilImage(
            onClick = {
                onClickImage()
            },
            onLongClick = {
                if (longClickEnabled) isDialogVisible.value = true
            },
            imageUrl = postContentModel.imageUrl,
            imageRatio = imageRatio.floatValue,
            updateImageRatio = { imageRatio.floatValue = it }
        )
    }

    if (isDialogVisible.value) {
        DeleteDialog(
            onClickCancel = { isDialogVisible.value = false },
            onClickConfirm = {
                onLongClickImage()
                isDialogVisible.value = false
            }
        )
    }
}


@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    onClickCancel: () -> Unit = {},
    onClickConfirm: (String) -> Unit = {},
) {
    val textValue = remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = { onClickCancel() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        Card(
            modifier = modifier.size(
                width = 250.dp,
                height = 130.dp
            ),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.string_question_delete),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    TextButton(
                        onClick = onClickCancel,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(text = stringResource(R.string.string_cancel), color = Error)
                    }
                    TextButton(
                        onClick = {
                            onClickConfirm(textValue.value)
                            onClickCancel()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(text = stringResource(R.string.string_remove), color = Primary)
                    }
                }
            }
        }
    }
}
