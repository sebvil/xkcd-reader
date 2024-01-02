package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.savedstate.SavedStateRegistryOwner
import coil.compose.AsyncImage
import com.colibrez.xkcdreader.android.XkcdReaderApplication
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination
@RootNavGraph(start = true)
@Composable
fun ComicListScreen(
    viewModel: ComicListViewModel = comicListViewModel(),
    navigator: DestinationsNavigator
) {

    val navigationState by viewModel.navigationState.collectAsState()

    LaunchedEffect(key1 = navigationState) {
        navigationState?.let {
            navigator.navigate(it.direction, onlyIfResumed = true)
            viewModel.resetNavigationState()
        }
    }

    val state by viewModel.state.collectAsState()

    val lazyPagingItems = state.comics.collectAsLazyPagingItems()

    val image: @Composable (imageUrl: String) -> Unit = { imageUrl ->
        var loading by remember {
            mutableStateOf(true)
        }
        if (loading) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onSurface)
                    .size(64.dp)
            )
        }
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.sizeIn(minWidth = 64.dp, maxHeight = 64.dp, maxWidth = 64.dp),
            onSuccess = {
                loading = false
            },
            onError = {
                loading = false
            }
        )
    }

    LazyColumn {
        items(
            lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.comicNumber },
            contentType = lazyPagingItems.itemContentType { ListComic::class.hashCode() }
        ) { index ->
            val item = lazyPagingItems[index] ?: return@items
            ListItem(
                headlineContent = {
                    Text(
                        text = "${item.comicNumber}. ${item.title}",
                        fontWeight = if (item.isRead) null else FontWeight.ExtraBold
                    )
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        viewModel.handle(
                            ComicListUserAction.ComicClicked(
                                comicNum = item.comicNumber,
                                comicTitle = item.title
                            )
                        )
                    },
                leadingContent = {
                    image(item.imageUrl)
                }, trailingContent = {
                    IconButton(onClick = {
                        viewModel.handle(
                            ComicListUserAction.ToggleFavorite(
                                comicNum = item.comicNumber,
                                isFavorite = item.isFavorite
                            )
                        )
                    }) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Mark as favorite",
                            tint = if (item.isFavorite) Color.Yellow else LocalContentColor.current
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalPagingApi::class)
@Composable
fun comicListViewModel(savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current): ComicListViewModel {
    val dependencyContainer =
        (LocalContext.current.applicationContext as XkcdReaderApplication).dependencyContainer

    val factory = ComicListViewModel.Factory(
        owner = savedStateRegistryOwner,
        comicsRemoteMediator = dependencyContainer.comicsRemoteMediator,
        pagingSourceFactory = dependencyContainer.comicPagingSourceFactory,
        comicRepository = dependencyContainer.comicRepository,
    )
    return viewModel(factory = factory)
}