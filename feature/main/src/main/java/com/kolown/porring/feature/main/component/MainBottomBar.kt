package com.kolown.porring.feature.main.component

import android.content.res.Resources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.PrimaryDark
import com.kolown.porring.core.designsystem.ui.theme.PrimaryUnActive
import com.kolown.porring.core.ui.ext.shadow
import com.kolown.porring.feature.main.navigation.MainMenu
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun MainBottomBar(
    visible: Boolean,
    menus: PersistentList<MainMenu>,
    currentMenu: MainMenu?,
    onMenuSelected: (MainMenu) -> Unit = {},
    onCameraSelected: () -> Unit = {},
) {
    val density = LocalDensity.current
    val insets = WindowInsets.systemBars
    val navBarHeight = with(density) { insets.getBottom(this).toDp() }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
            initialOffsetY = { it }
        ) + fadeIn(),
        exit = slideOutVertically(
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
            targetOffsetY = { it }
        ) + fadeOut()
    ) {
        Column {
            Box {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                ) {
                    GradientFloatingActionButton(
                        item = menus[2],
                        onClick = onCameraSelected
                    )

                    Spacer(modifier = Modifier.height(30.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .align(Alignment.BottomCenter)
                        .shadow(
                            color = Color(0xFF598AFF).copy(alpha = 0.1f),
                            blur = 8.dp,
                            offsetY = (-5).dp,
                            shape = menuBarShape(isShadow = true),
                        )
                        .background(
                            color = PorringTheme.colors.surface,
                            shape = menuBarShape()
                        ),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomBarItem(menus[0], currentMenu == menus[0], onMenuSelected)
                    BottomBarItem(menus[1], currentMenu == menus[1], onMenuSelected)
                    Spacer(modifier = Modifier.weight(1f))
                    BottomBarItem(menus[3], currentMenu == menus[3], onMenuSelected)
                    BottomBarItem(menus[4], currentMenu == menus[4], onMenuSelected)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(navBarHeight)
                    .background(PorringTheme.colors.surface)
            )
        }
    }
}

@Composable
private fun RowScope.BottomBarItem(
    item: MainMenu,
    isSelected: Boolean,
    onNavigateTo: (MainMenu) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .selectable(
                selected = isSelected,
                onClick = { onNavigateTo(item) },
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .fillMaxHeight()
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(item.iconResId),
            contentDescription = item.contentDescription,
            tint = if (isSelected) PorringTheme.colors.primary else PorringTheme.colors.tertiary,
        )
    }
}

@Composable
private fun GradientFloatingActionButton(
    item: MainMenu,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(CircleShape)
            .background(brush = Brush.verticalGradient(listOf(PrimaryDark, Primary)))
            .clickable(
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,//TODO 아이콘 추가 바람
            contentDescription = item.contentDescription,
            tint = Color.White,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private fun Dp.toPx() =
    with(Resources.getSystem().displayMetrics.density) { this@toPx.value * this }

private fun menuBarShape(isShadow: Boolean = false) = GenericShape { size, _ ->
    reset()

    moveTo(0f, 0f)

    val width = 250f
    val height = 40.dp.toPx()

    val point1 = 100f
    val point2 = 110f

    lineTo(size.width / 2 - width, 0f)

    cubicTo(
        size.width / 2 - point1, 0f,
        size.width / 2 - point2, height,
        size.width / 2, height
    )

    cubicTo(
        size.width / 2 + point2, height,
        size.width / 2 + point1, 0f,
        size.width / 2 + width, 0f
    )

    lineTo(size.width / 2 + width, 0f)

    lineTo(size.width, 0f)
    lineTo(size.width, size.height)
    if (!isShadow) {
        lineTo(0f, size.height)
    }

    close()
}

@Preview
@Composable
private fun Preview() {
    var currentItem by remember { mutableStateOf(MainMenu.HOME) }
    MainBottomBar(
        visible = true,
        menus = MainMenu.entries.toPersistentList(),
        currentMenu = currentItem,
        onMenuSelected = { currentItem = it },
        onCameraSelected = {}
    )
}

@Preview
@Composable
private fun FloatingPreview() {
    GradientFloatingActionButton(
        item = MainMenu.CAMERA,
        onClick = {}
    )
}