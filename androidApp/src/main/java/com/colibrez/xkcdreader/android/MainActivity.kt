package com.colibrez.xkcdreader.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.compose.AsyncImage
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.colibrez.xkcdreader.Greeting
import com.colibrez.xkcdreader.android.repository.ComicsPagingSource
import com.colibrez.xkcdreader.android.repository.ComicsRemoteMediator
import com.colibrez.xkcdreader.data.DriverFactory
import com.colibrez.xkcdreader.data.createDatabase
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.ApiClient
import com.colibrez.xkcdreader.repository.ComicRepository
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val comicQueries = createDatabase(DriverFactory(this)).comicQueries
        val apiClient = ApiClient(Dispatchers.IO)
        val comicRepository = ComicRepository(comicQueries, Dispatchers.IO)
        MainViewModel.Factory(
            comicsRemoteMediator = ComicsRemoteMediator(
                comicRepository = ComicRepository(
                    comicQueries = comicQueries,
                    ioDispatcher = Dispatchers.IO
                ),
                apiClient = apiClient
            ),
            comicRepository = comicRepository
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
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
                    color = MaterialTheme.colors.background
                ) {
                    val lazyPagingItems = viewModel.pagedComics.collectAsLazyPagingItems()

                    val image: @Composable (item: Comic) -> Unit = {item ->
                        var loading by remember {
                            mutableStateOf(true)
                        }
                        if (loading) {
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colors.onSurface)
                                    .size(64.dp)
                            )
                        }
                        AsyncImage(
                            model = item.img,
                            contentDescription = "Comic ${item.num} preview",
                            modifier = Modifier.sizeIn(maxHeight = 64.dp, maxWidth = 64.dp),
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
                                modifier = Modifier.padding(vertical = 8.dp),
                                icon = {
                                    image(item)
                                }
                            ) {
                                Text(text = "${item.num}. ${item.title}")
                            }
                        }
                    }
                }
            }
        }
    }
}
