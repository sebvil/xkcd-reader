package com.colibrez.xkcdreader.android.ui.features.comic

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import com.colibrez.xkcdreader.android.ui.components.FavoriteButton
import com.colibrez.xkcdreader.android.ui.components.images.ZoomableImage
import com.colibrez.xkcdreader.android.ui.core.navigation.Screen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File

@Destination(navArgsDelegate = ComicScreenArguments::class)
@Composable
fun ComicScreen(
    navigator: DestinationsNavigator,
    viewModel: ComicViewModel = comicViewModel()
) {
    Screen(viewModel = viewModel, navigator = navigator) { state, handleUserAction ->
        ComicLayout(state = state, handleUserAction = handleUserAction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicLayout(
    state: ComicState,
    modifier: Modifier = Modifier,
    handleUserAction: (ComicUserAction) -> Unit
) {
    var imageFile: File? by remember {
        mutableStateOf(null)
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "${state.comicNumber}. ${state.comicTitle}")
                },
                navigationIcon = {
                    IconButton(onClick = { handleUserAction(ComicUserAction.BackButtonClicked) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    (state as? ComicState.Data)?.let {
                        ComicTopBarActions(
                            state = it,
                            imageFile = imageFile,
                            handleUserAction = handleUserAction,
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when (state) {
            is ComicState.Data -> {
                ComicBody(
                    state = state,
                    paddingValues = paddingValues,
                    handleUserAction = handleUserAction,
                    setImageFile = { imageFile = it },
                )
            }

            is ComicState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ComicBody(
    state: ComicState.Data,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    handleUserAction: (ComicUserAction) -> Unit = {},
    setImageFile: (File) -> Unit = {}
) {
    if (state.showDialog) {
        AlertDialog(
            onDismissRequest = { handleUserAction(ComicUserAction.OverlayClicked) },
        ) {
            Text(text = state.altText)
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center,
    ) {
        val context = LocalContext.current
        ZoomableImage(
            imageUrl = state.imageUrl,
            modifier = Modifier.fillMaxSize(),
            contentDescription = state.imageDescription,
            onClick = { handleUserAction(ComicUserAction.ImageClicked) },
            onSuccess = {
                context.imageLoader.diskCache?.also { cache ->
                    it.result.diskCacheKey?.also { key ->
                        cache.openSnapshot(key).use { snapshot ->
                            val imageKey = Uri.parse(key).pathSegments.last()
                            snapshot?.data?.toFile()?.copyTo(
                                File(
                                    /* parent = */
                                    context.cacheDir.resolve("image_cache"),
                                    /* child = */
                                    imageKey,
                                ),
                                overwrite = true,
                            )?.let { file -> setImageFile(file) }
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun RowScope.ComicTopBarActions(
    state: ComicState.Data,
    imageFile: File?,
    handleUserAction: (ComicUserAction) -> Unit = {}
) {
    val context = LocalContext.current
    FavoriteButton(
        isFavorite = state.isFavorite,
        onFavoriteChanged = {
            handleUserAction(
                ComicUserAction.ToggleFavorite(
                    comicNum = state.comicNumber,
                    isFavorite = it,
                ),
            )
        },
    )

    IconButton(onClick = {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND

            imageFile?.let {
                val contentUri: Uri = getUriForFile(
                    /* context = */
                    context,
                    /* authority = */
                    "com.colibrez.xkcdreader",
                    /* file = */
                    it,
                )
                clipData =
                    ClipData.newUri(context.contentResolver, "", contentUri)
                putExtra(Intent.EXTRA_STREAM, contentUri)

                putExtra(
                    Intent.EXTRA_TEXT,
                    """
                    ${state.comicNumber}. ${state.comicTitle}
                    
                    "${state.altText}"
                    
                    ${state.permalink}
                    """.trimIndent(),
                )
                type = "image/*"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(context, shareIntent, null)
    }) {
        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
    }
}

@Composable
fun comicViewModel(
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
): ComicViewModel {
    val dependencyContainer =
        (LocalContext.current.applicationContext as XkcdReaderApplication).dependencyContainer
    val factory = ComicViewModel.Factory(
        owner = savedStateRegistryOwner,
        comicRepository = dependencyContainer.comicRepository,
    )
    return viewModel(factory = factory)
}
