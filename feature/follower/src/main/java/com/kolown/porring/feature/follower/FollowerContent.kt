package com.kolown.porring.feature.follower

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.ui.component.CoilImage
import com.kolown.porring.core.ui.component.FollowDialog
import com.kolown.porring.core.designsystem.ui.theme.Error
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.model.FollowerThumbnail
import com.kolown.porring.core.model.PostContentModel

@Composable
internal fun FollowContent(
    followerThumbnail: FollowerThumbnail,
    navigateToTheir: () -> Unit = {},
    updateFollowerThumbnail: (FollowerThumbnail) -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                navigateToTheir()
            }
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = followerThumbnail.followerName,
                color = Primary,
                style = MaterialTheme.typography.titleLarge
            )
            TextButton (
                onClick = { updateFollowerThumbnail(followerThumbnail) }
            ) {
                Text(
                    text = stringResource(R.string.string_edit),
                    color = Error,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
        ) {
            items(followerThumbnail.posts) { imageUrl ->
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(10.dp)),
                ) {
                    CoilImage(
                        imageUrl = imageUrl,
                        imageRatio = 1f,
                        isClickedEnabled = false
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFollowContent() {
    FollowContent(FollowerThumbnail.dummy)
}
