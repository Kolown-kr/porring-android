package com.kolown.porring.feature.upload

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.kolown.porring.core.designsystem.R.drawable
import com.kolown.porring.core.designsystem.component.PorringIconButton
import com.kolown.porring.core.designsystem.component.PorringTextField
import com.kolown.porring.core.designsystem.component.PorringTopAppBar
import com.kolown.porring.core.designsystem.ui.theme.Error
import com.kolown.porring.core.designsystem.ui.theme.Gray
import com.kolown.porring.core.designsystem.ui.theme.Primary
import com.kolown.porring.core.designsystem.ui.theme.PrimaryUnActive
import com.kolown.porring.feature.upload.component.CategoryGroup

@Composable
internal fun UploadRoute(
    viewModel: UploadViewModel = hiltViewModel(),
    imgUri: String,
    padding: PaddingValues,
    navigateToHome: () -> Unit,
) {
    val uploadState by viewModel.uploadState.collectAsStateWithLifecycle()
    val uploadEnable by viewModel.uploadEnable.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val previousSize = remember { mutableIntStateOf(uploadState.categoryItems.size) }

    val scrollState = rememberScrollState()
    var imeHeightState by remember { mutableIntStateOf(0) }
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
    var isDescriptionMax by remember { mutableStateOf(false) }

    BackHandler {
        navigateToHome()
    }

    LaunchedEffect(Unit) {
        if (imgUri.isNotBlank()) {
            viewModel.getUriWebP(imgUri)
        }
    }

    LaunchedEffect(imeHeight) {
        imeHeightState = imeHeight
        scrollState.scrollTo(imeHeight)
    }

    LaunchedEffect(uploadState.categoryItems.size) {
        if (uploadState.categoryItems.size > previousSize.intValue) {
            focusRequester.requestFocus()
        }
        previousSize.intValue = uploadState.categoryItems.size
    }

    UploadScreen(
        imeHeightState = imeHeightState,
        description = uploadState.description,
        uploadEnable = uploadEnable,
        isDescriptionMax = isDescriptionMax,
        padding = padding,
        scrollState = scrollState,
        focusManager = focusManager,
        focusRequester = focusRequester,
        imgUri = imgUri.ifBlank { uploadState.imgUri },
        categoryItems = uploadState.categoryItems,
        uploadPost = viewModel::uploadPost,
        addCategory = viewModel::addCategory,
        navigateToHome = navigateToHome,
        removeCategory = viewModel::removeCategory,
        changeDescription = viewModel::changeDescription,
        updateIsDescriptionMax = { isDescriptionMax = it },
        changeCategoryName = viewModel::changeCategoryName
    )
}

@Composable
private fun UploadScreen(
    imeHeightState: Int = 0,
    description: String = "",
    uploadEnable: Boolean = false,
    isDescriptionMax: Boolean = false,
    padding: PaddingValues = PaddingValues(),
    scrollState: ScrollState = rememberScrollState(),
    focusManager: FocusManager = LocalFocusManager.current,
    focusRequester: FocusRequester = remember { FocusRequester() },
    imgUri: String = stringResource(R.string.string_mock_uri),
    categoryItems: List<String> = emptyList(),
    uploadPost: () -> Unit = {},
    addCategory: () -> Unit = {},
    navigateToHome: () -> Unit = {},
    removeCategory: (String) -> Unit = {},
    changeDescription: (String) -> Unit = {},
    updateIsDescriptionMax: (Boolean) -> Unit = {},
    changeCategoryName: (Int, String) -> Unit = { _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        PorringTopAppBar(
            title = stringResource(R.string.string_new_post),
            navigationIcon = {
                PorringIconButton(
                    icon = ImageVector.vectorResource(drawable.ic_arrow_back),
                    onClick = navigateToHome,
                    contentDescription = stringResource(R.string.string_go_back)
                )
            }
        )

        UploadContent(
            imgUri = imgUri,
            description = description,
            imeHeightState = imeHeightState,
            uploadEnable = uploadEnable,
            isDescriptionMax = isDescriptionMax,
            categoryItems = categoryItems,
            scrollState = scrollState,
            focusManager = focusManager,
            focusRequester = focusRequester,
            uploadPost = uploadPost,
            addCategory = addCategory,
            navigateToHome = navigateToHome,
            removeCategory = removeCategory,
            changeDescription = changeDescription,
            changeCategoryName = changeCategoryName,
            updateIsDescriptionMax = updateIsDescriptionMax
        )
    }
}

@Composable
private fun UploadContent(
    imgUri: String,
    description: String,
    imeHeightState: Int,
    uploadEnable: Boolean,
    isDescriptionMax: Boolean,
    categoryItems: List<String>,
    scrollState: ScrollState,
    focusManager: FocusManager,
    focusRequester: FocusRequester,
    uploadPost: () -> Unit,
    addCategory: () -> Unit,
    navigateToHome: () -> Unit,
    removeCategory: (String) -> Unit,
    changeDescription: (String) -> Unit,
    changeCategoryName: (Int, String) -> Unit,
    updateIsDescriptionMax: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 24.dp)
                .imePadding()
                .verticalScroll(scrollState)
                .focusable(true),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp)),
                model = imgUri,
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.string_upload_image)
            )

            EventColumn(
                description = description,
                isDescriptionMax = isDescriptionMax,
                focusManager = focusManager,
                updateIsDescriptionMax = updateIsDescriptionMax,
                changeDescription = changeDescription
            )

            CategoryGroup(
                categoryItems = categoryItems,
                focusManager = focusManager,
                focusRequester = focusRequester,
                addCategory = addCategory,
                removeCategory = removeCategory,
                changeCategoryName = changeCategoryName
            )
        }

        AnimatedVisibility(
            visible = imeHeightState < 1,
            enter = fadeIn(),
            exit = ExitTransition.None
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(40.dp),
                onClick = {
                    uploadPost()
                    navigateToHome()
                },
                enabled = uploadEnable,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (uploadEnable) Primary else PrimaryUnActive)
            ) {
                Text(
                    text = stringResource(R.string.string_upload),
                    color = if (uploadEnable) Color.White else Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun EventColumn(
    description: String,
    isDescriptionMax: Boolean,
    focusManager: FocusManager,
    updateIsDescriptionMax: (Boolean) -> Unit,
    changeDescription: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top
    ) {
        PorringTextField(
            value = description,
            onValueChange = {
                changeDescription(it.substring(0, minOf(20, it.length)))
                updateIsDescriptionMax(it.length > 20)
            },
            hint = stringResource(R.string.string_input_description),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            trailingIcon = {
                PorringIconButton(
                    icon = ImageVector.vectorResource(R.drawable.ic_cancel_circle),
                    onClick = { changeDescription("") }
                )
            },
            maxLine = 3,
        )

        if (isDescriptionMax) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.string_max_description),
                style = MaterialTheme.typography.bodySmall,
                color = Error
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewUploadScreen() {
    UploadScreen()
}