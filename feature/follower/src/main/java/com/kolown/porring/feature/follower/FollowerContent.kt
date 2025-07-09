package com.kolown.porring.feature.follower

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.designsystem.ui.theme.PorringTypography
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.model.FollowWithThumbnail
import com.kolown.porring.core.ui.component.CoilImage

@Composable
internal fun FollowContent(
    followWithThumbnail: FollowWithThumbnail,
    onClickUnfollow: (String) -> Unit = {},
    navigateToTheir: () -> Unit = {},
    updateFollowerThumbnail: (FollowWithThumbnail) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PorringTheme.colors.surface)
            .border(
                width = 1.dp,
                color = PorringTheme.colors.outlineVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(16.dp)
            .clickable(onClick = navigateToTheir),
        verticalArrangement = Arrangement.Center
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val imageSize = (maxWidth - 16.dp) / 3

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
            ) {
                items(followWithThumbnail.thumbnails) { imageUrl ->
                    Card(
                        modifier = Modifier
                            .size(imageSize)
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


        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = followWithThumbnail.followerName,
                color = Primary,
                style = PorringTypography.title
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.string_edit),
                    color = PorringTheme.colors.onSurface,
                    style = PorringTheme.typography.label,
                    modifier = Modifier.clickable { updateFollowerThumbnail(followWithThumbnail) }
                )
                Text(
                    text = stringResource(R.string.string_un_follow),
                    color = PorringTheme.colors.error,
                    style = PorringTheme.typography.label,
                    modifier = Modifier.clickable { onClickUnfollow(followWithThumbnail.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFollowContent() {
    FollowContent(FollowWithThumbnail.dummy)
}
