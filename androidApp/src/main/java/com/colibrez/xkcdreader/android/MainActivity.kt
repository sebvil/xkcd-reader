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
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.savedstate.SavedStateRegistryOwner
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
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import java.time.format.TextStyle

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
fun mainViewModel(
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current,

    ): MainViewModel {
    val driverFactory = DriverFactory(LocalContext.current)
    val database = createDatabase(driverFactory)
    val driver = driverFactory.driver
    val comicQueries = database.comicEntityQueries
    val apiClient = ApiClient(Dispatchers.IO)
    val comicRepository = ComicRepository(
        comicQueries = comicQueries,
        readComicsQueries = database.readComicEntityQueries,
        userEntityQueries = database.userEntityQueries,
        ioDispatcher = Dispatchers.IO
    )
    val factory = MainViewModel.Factory(
        savedStateRegistryOwner,
        comicsRemoteMediator = ComicsRemoteMediator(
            comicRepository = comicRepository,
            apiClient = apiClient
        ),
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
                    Text(text = "${item.num}. ${item.title}", fontWeight = if (item.isRead) null else FontWeight.ExtraBold)
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable {
                        navigator.navigate(ComicScreenDestination(item.num))
                    },
                leadingContent = {
                    image(item)
                }
            )
        }
    }
}