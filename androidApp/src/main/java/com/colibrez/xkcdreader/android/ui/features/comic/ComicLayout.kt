package com.colibrez.xkcdreader.android.ui.features.comic

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.colibrez.xkcdreader.android.ui.components.FavoriteButton
import com.colibrez.xkcdreader.android.ui.components.images.ZoomableImage
import com.colibrez.xkcdreader.android.util.webpage.InBrowserWebPageViewer
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicLayout(
    state: ComicState,
    hasBackButton: Boolean,
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
                    Text(text = state.comicNumber?.let { "xkcd $it" }.orEmpty())
                },
                navigationIcon = {
                    if (hasBackButton) {
                        IconButton(onClick = { handleUserAction(ComicUserAction.BackButtonClicked) }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
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
        BasicAlertDialog(
            modifier = Modifier
                .background(
                    color = AlertDialogDefaults.containerColor,
                    shape = AlertDialogDefaults.shape,
                )
                .padding(28.dp),
            onDismissRequest = { handleUserAction(ComicUserAction.OverlayClicked) },
        ) {
            Text(text = state.altText)
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val context = LocalContext.current
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f),
        ) {
            Text(
                text = state.comicTitle,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
            )
        }
        ZoomableImage(
            imageUrl = state.imageUrl,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            IconButton(
                onClick = {
                    state.firstComic?.also {
                        handleUserAction(ComicUserAction.NavigateToComic(it))
                    }
                },
                enabled = state.firstComic != state.comicNumber && state.firstComic != null
            ) {
                Icon(imageVector = Icons.Default.FirstPage, contentDescription = "First comic")
            }

            IconButton(
                onClick = {
                    state.previousComic?.also {
                        handleUserAction(ComicUserAction.NavigateToComic(it))
                    }
                },
                enabled = state.previousComic != null
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                    contentDescription = "Previous comic"
                )
            }

            IconButton(
                onClick = {
                    state.nextComic?.also {
                        handleUserAction(ComicUserAction.NavigateToComic(it))
                    }
                },
                enabled = state.nextComic != null
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                    contentDescription = "Next comic"
                )
            }


            IconButton(
                onClick = {
                    state.lastComic?.also {
                        handleUserAction(ComicUserAction.NavigateToComic(it))
                    }
                },
                enabled = state.lastComic != state.comicNumber && state.lastComic != null
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.LastPage,
                    contentDescription = "Last comic"
                )
            }
        }
    }
}

@Composable
private fun RowScope.ComicTopBarActions(
    state: ComicState.Data,
    imageFile: File?,
    handleUserAction: (ComicUserAction) -> Unit = {}
) {
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
    ShareButton(state = state, imageFile = imageFile)
    OverflowMenu(state)
}

@Composable
private fun OverflowMenu(state: ComicState.Data, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val webPageViewer = remember {
        InBrowserWebPageViewer(context)
    }

    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart),
    ) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("View on xkcd.com") },
                onClick = {
                    webPageViewer.viewWebPage(state.permalink)
                },
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                    )
                },
            )
            DropdownMenuItem(
                text = { Text("Explain xkcd") },
                onClick = {
                    webPageViewer.viewWebPage(state.explainXckdPermalink)
                },
                leadingIcon = {
                    Icon(
                        Icons.AutoMirrored.Outlined.OpenInNew,
                        contentDescription = null,
                    )
                },
            )
        }
    }
}

@Composable
private fun ShareButton(
    state: ComicState.Data,
    imageFile: File?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND

                imageFile?.let {
                    val contentUri: Uri = FileProvider.getUriForFile(
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
            ContextCompat.startActivity(context, shareIntent, null)
        },
        modifier = modifier,
    ) {
        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
    }
}
