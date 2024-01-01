package com.colibrez.xkcdreader.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.savedstate.SavedStateRegistryOwner
import app.cash.sqldelight.paging3.QueryPagingSource
import coil.Coil
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.colibrez.xkcdreader.android.destinations.ComicScreenDestination
import com.colibrez.xkcdreader.android.data.repository.ComicsRemoteMediator
import com.colibrez.xkcdreader.database.DriverFactory
import com.colibrez.xkcdreader.database.createDatabase
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.ApiClient
import com.colibrez.xkcdreader.data.repository.OfflineFirstComicRepository
import com.colibrez.xkcdreader.database.SqlDelightLocalComicDataSource
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Coil.setImageLoader {
            ImageLoader.Builder(this)
                .memoryCache {
                    MemoryCache.Builder(this).build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(this.cacheDir.resolve("image_cache")).build()
                }
                .build()
        }
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DestinationsNavHost(navGraph = NavGraphs.root)
                }
            }
        }
    }
}

@Composable
fun mainViewModel(savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current): MainViewModel {
    val driverFactory = DriverFactory(LocalContext.current)
    val database = createDatabase(driverFactory)
    val apiClient = ApiClient(Dispatchers.IO)
    val comicRepository = OfflineFirstComicRepository(
        SqlDelightLocalComicDataSource(
            ioDispatcher = Dispatchers.IO,
            database = database
        )
    )
    val mediator = ComicsRemoteMediator(
        comicRepository = comicRepository,
        apiClient = apiClient
    )
    val factory = MainViewModel.Factory(
        savedStateRegistryOwner,
        comicsRemoteMediator = mediator,
        pagingSourceFactory = {
            QueryPagingSource(
                countQuery = database.comicEntityQueries.count(),
                transacter = database.comicEntityQueries,
                context = Dispatchers.IO,
                queryProvider = { limit, offset ->
                    database.comicEntityQueries.selectPaged(
                        limit,
                        offset,
                        OfflineFirstComicRepository::mapComicSelecting
                    )
                }
            ).also {
                mediator.invalidate = {
                    it.invalidate()
                }
            }
        },
        comicRepository = comicRepository
    )
    return viewModel(factory = factory)
}


@Destination(start = true)
@Composable
fun MainScreen(viewModel: MainViewModel = mainViewModel(), navigator: DestinationsNavigator) {
    val lazyPagingItems = viewModel.pagedComics.collectAsLazyPagingItems()

    val image: @Composable (item: Comic) -> Unit = { item ->
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
            model = item.img,
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
            key = lazyPagingItems.itemKey { it.num }
        ) { index ->
            val item = lazyPagingItems[index] ?: return@items
            ListItem(
                headlineContent = {
                    Text(
                        text = "${item.num}. ${item.title}",
                        fontWeight = if (item.isRead) null else FontWeight.ExtraBold
                    )
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        navigator.navigate(ComicScreenDestination(item.num))
                    },
                leadingContent = {
                    image(item)
                }, trailingContent = {
                    IconButton(onClick = {
                        viewModel.handle(
                            MainUserAction.ToggleFavorite(
                                item.num,
                                item.isFavorite
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