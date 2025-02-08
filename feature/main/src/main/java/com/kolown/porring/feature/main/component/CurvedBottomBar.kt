package com.kolown.porring.feature.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kolown.porring.core.designsystem.ui.theme.PorringTheme
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.PrimaryUnActive
import com.kolown.porring.feature.main.navigation.MainMenu
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun CurvedBottomBar(
    menuItems: PersistentList<MainMenu>,
    currentItem: MainMenu,
    onNavigateTo: (MainMenu) -> Unit,
) {
    Box {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
        ) {
            FloatingActionButton(
                shape = CircleShape,
                containerColor = Color.White,
                contentColor = Color.Gray,
                onClick = { onNavigateTo(menuItems[2]) },
            ) {
                Row(
                    modifier = Modifier.size(64.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CurvedBottomBarItem(menuItems[2], currentItem == menuItems[2])
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.White,
                    shape = menuBarShape()
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurvedBottomBarItem(menuItems[0], currentItem == menuItems[0], onNavigateTo)
            CurvedBottomBarItem(menuItems[1], currentItem == menuItems[1], onNavigateTo)
            Spacer(modifier = Modifier.weight(1f))
            CurvedBottomBarItem(menuItems[3], currentItem == menuItems[3], onNavigateTo)
            CurvedBottomBarItem(menuItems[4], currentItem == menuItems[4], onNavigateTo)
        }
    }
}

@Composable
private fun RowScope.CurvedBottomBarItem(
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
                indication = remember { ripple(radius = 32.dp) }
            )
            .fillMaxHeight()
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(item.iconResId),
            contentDescription = item.contentDescription,
            tint = if (isSelected) Primary else PrimaryUnActive,
        )
    }
}

private fun menuBarShape() = GenericShape { size, _ ->
    reset()

    moveTo(0f, 0f)

    val width = 250f
    val height = 90f

    val point1 = 75f
    val point2 = 85f

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
    lineTo(0f, size.height)

    close()
}

@Preview
@Composable
private fun Preview() {
    var currentItem by remember { mutableStateOf(MainMenu.HOME) }
    PorringTheme(true) {
        CurvedBottomBar(
            menuItems = MainMenu.entries.toPersistentList(),
            currentItem = currentItem,
            onNavigateTo = { currentItem = it }
        )
    }
}
