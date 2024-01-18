package com.colibrez.xkcdreader.android.ui.features.latest

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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

@Destination
@Composable
fun LatestComicScreen(
    navigator: DestinationsNavigator,
    viewModel: LatestComicViewModel = latestComicViewModel()
) {
    Screen(viewModel = viewModel, navigator = navigator) { state, handleUserAction ->
        LatestComicLayout(state = state, handleUserAction = handleUserAction)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LatestComicLayout(
    state: LatestComicState,
    modifier: Modifier = Modifier,
    handleUserAction: (LatestComicUserAction) -> Unit
) {
    var imageFile: File? by remember {
        mutableStateOf(null)
    }
    when (state) {
        is LatestComicState.Data -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "${state.comicNumber}. ${state.comicTitle}")
                        },
                        actions = {
                            LatestComicTopBarActions(
                                state = state,
                                imageFile = imageFile,
                                handleUserAction = handleUserAction,
                            )
                        },
                    )
                },
            ) { paddingValues ->

                ComicBody(
                    state = state,
                    paddingValues = paddingValues,
                    handleUserAction = handleUserAction,
                    setImageFile = { imageFile = it },
                )
            }
        }

        is LatestComicState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ComicBody(
    state: LatestComicState.Data,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    handleUserAction: (LatestComicUserAction) -> Unit = {},
    setImageFile: (File) -> Unit = {}
) {
    if (state.showDialog) {
        AlertDialog(
            onDismissRequest = { handleUserAction(LatestComicUserAction.OverlayClicked) },
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
            onClick = { handleUserAction(LatestComicUserAction.ImageClicked) },
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
private fun RowScope.LatestComicTopBarActions(
    state: LatestComicState.Data,
    imageFile: File?,
    handleUserAction: (LatestComicUserAction) -> Unit = {}
) {
    val context = LocalContext.current
    FavoriteButton(isFavorite = state.isFavorite, onFavoriteChanged = {
        handleUserAction(
            LatestComicUserAction.ToggleFavorite(
                comicNum = state.comicNumber,
                isFavorite = it,
            ),
        )
    })

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
fun latestComicViewModel(
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
): LatestComicViewModel {
    val dependencyContainer =
        (LocalContext.current.applicationContext as XkcdReaderApplication).dependencyContainer
    val factory = LatestComicViewModel.Factory(
        owner = savedStateRegistryOwner,
        comicRepository = dependencyContainer.comicRepository,
    )
    return viewModel(factory = factory)
}
