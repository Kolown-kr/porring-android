package com.kolown.porring.feature.home.component

import IconFollow
import IconGallery
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.model.PostContentModel
import com.kolown.porring.core.ui.component.CoilImage

@Composable
fun IconButtonGroup(
    modifier: Modifier,
    imageItem: PostContentModel,
    onFollowClick: () -> Unit,
    navigateToTheir: () -> Unit,
) {
    val whiteModifier = Modifier
        .clip(CircleShape)
        .size(48.dp)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomIconButton(
            modifier = whiteModifier,
            imageVector = IconFollow,
            isSelected = imageItem.isFollower
        ) {
            onFollowClick()
        }
        CustomIconButton(
            modifier = whiteModifier,
            imageVector = IconGallery,
            isSelected = false,
            onClick = navigateToTheir
        )
    }
}

@Composable
fun RandomImage(
    imageUrl: String = "https://echo.unicomm.fsu.edu/3.3/img/placeholders/ratio-4-5.png",
    onClickImage: () -> Unit,
) {
    val imageRatio = remember { mutableFloatStateOf(4f / 5f) }

    Card(
        modifier = Modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        CoilImage(
            imageUrl = imageUrl,
            imageRatio = imageRatio.floatValue,
            updateImageRatio = { imageRatio.floatValue = it },
            onClick = onClickImage
        )
    }


}

@Composable
private fun CustomIconButton(
    modifier: Modifier,
    imageVector: ImageVector,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier.background(if (isSelected) Primary else Color.White),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = imageVector,
            contentDescription = null,
            tint = if (isSelected) Color.White else Primary
        )
    }
}
