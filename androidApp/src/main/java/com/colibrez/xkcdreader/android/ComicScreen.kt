package com.colibrez.xkcdreader.android

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider.getUriForFile
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.SavedStateRegistryOwner
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.imageLoader
import com.colibrez.xkcdreader.database.DriverFactory
import com.colibrez.xkcdreader.database.createDatabase
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import java.io.File


@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Destination
@Composable
fun ComicScreen(
    num: Long,
    navigator: DestinationsNavigator,
    viewModel: ComicViewModel = comicViewModel(num = num)
) {
    val state by viewModel.state.collectAsState()

    var showDialog by remember {
        mutableStateOf(false)
    }

    state?.let { stateNotNull ->

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
            ) {
                Text(text = stateNotNull.alt)
            }
        }

        val context = LocalContext.current
        var imageFile: File? by remember {
            mutableStateOf(null)
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "${stateNotNull.num}. ${stateNotNull.title}")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.navigateUp() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND

                                imageFile?.let {
                                    val contentUri: Uri = getUriForFile(
                                        /* context = */ context,
                                        /* authority = */ "com.colibrez.xkcdreader",
                                        /* file = */ it
                                    )
                                    clipData =
                                        ClipData.newUri(context.contentResolver, "", contentUri)
                                    putExtra(Intent.EXTRA_STREAM, contentUri)

                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${stateNotNull.num}. ${stateNotNull.title}\n\n${stateNotNull.alt}\n\n\n${stateNotNull.permalink}"
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
                    })
            }
        ) { paddingValues ->
            var scale by remember { mutableFloatStateOf(1f) }
            var offsetX by remember { mutableFloatStateOf(0f) }
            var offsetY by remember { mutableFloatStateOf(0f) }
            var size by remember {
                mutableStateOf(Size(0f, 0f))
            }
            var imageSize by remember {
                mutableStateOf(Size(0f, 0f))
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                Alignment.Center
            ) {
                AsyncImage(
                    model = stateNotNull.img,
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTransformGestures(
                                onGesture = { centroid, pan, gestureZoom, _ ->
                                    val oldScale = scale
                                    val newScale = (scale * gestureZoom).coerceIn(1f, 10f)
                                    val offset = (
                                            Offset(
                                                x = offsetX,
                                                y = offsetY
                                            ) + centroid / oldScale) - (centroid / newScale + pan / oldScale)

                                    scale = newScale
                                    val widthRatio =
                                        if (imageSize.width / imageSize.height >= size.width / size.height) {
                                            size.width / imageSize.width
                                        } else {
                                            size.height / imageSize.height
                                        }
                                    val actualWidth = imageSize.width * widthRatio * scale
                                    val maxOffsetX =
                                        if (imageSize.width / imageSize.height >= size.width / size.height) {
                                            (actualWidth - size.width) / scale
                                        } else {
                                            (size.width * scale - actualWidth) / 2 / scale + (actualWidth - size.width) / scale
                                        }

                                    val minOffsetX =
                                        if (imageSize.width / imageSize.height >= size.width / size.height) {
                                            0f
                                        } else {
                                            (size.width * scale - actualWidth) / 2 / scale
                                        }
                                    offsetX = if (actualWidth > size.width) {
                                        offset.x.coerceIn(
                                            minimumValue = minOffsetX,
                                            maximumValue = maxOffsetX
                                        )
                                    } else {
                                        (size.width * scale - size.width) / 2 / scale
                                    }

                                    val heightRatio =
                                        if (imageSize.height / imageSize.width >= size.height / size.width) {
                                            size.height / imageSize.height
                                        } else {
                                            size.width / imageSize.width
                                        }
                                    val actualHeight = imageSize.height * heightRatio * scale
                                    val maxOffsetY =
                                        if (imageSize.height / imageSize.width >= size.height / size.width) {
                                            (actualHeight - size.height) / scale
                                        } else {
                                            (size.height * scale - actualHeight) / 2 / scale + (actualHeight - size.height) / scale
                                        }

                                    val minOffsetY =
                                        if (imageSize.height / imageSize.width >= size.height / size.width) {
                                            0f
                                        } else {
                                            (size.height * scale - actualHeight) / 2 / scale
                                        }
                                    offsetY = if (actualHeight > size.height) {
                                        offset.y.coerceIn(
                                            minimumValue = minOffsetY,
                                            maximumValue = maxOffsetY
                                        )
                                    } else {
                                        (size.height * scale - size.height) / 2 / scale
                                    }

                                }
                            )
                        }
                        .clickable { showDialog = true }
                        .onSizeChanged { size = it.toSize() }
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = -offsetX * scale
                            translationY = -offsetY * scale
                            transformOrigin = TransformOrigin(0f, 0f)
                        }
                        .fillMaxSize(),
                    contentDescription = stateNotNull.transcript,
                    onSuccess = {
                        imageSize = it.painter.intrinsicSize
                        context.imageLoader.diskCache?.also { cache ->
                            it.result.diskCacheKey?.also { key ->
                                cache.openSnapshot(key).use { snapshot ->
                                    val imageKey = Uri.parse(key).pathSegments.last()
                                    imageFile = snapshot?.data?.toFile()
                                        ?.copyTo(
                                            File(
                                                context.cacheDir.resolve("image_cache"),
                                                imageKey
                                            ), overwrite = true
                                        )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun comicViewModel(
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
    num: Long
): ComicViewModel {
    val database = createDatabase(DriverFactory(LocalContext.current))
    val comicQueries = database.comicEntityQueries
    val comicRepository = ComicRepository(
        comicQueries = comicQueries,
        readComicsQueries = database.readComicEntityQueries,
        userEntityQueries = database.userEntityQueries,
        ioDispatcher = Dispatchers.IO
    )
    val factory = ComicViewModel.Factory(
        owner = savedStateRegistryOwner,
        comicRepository,
        num
    )
    return viewModel(factory = factory)
}
