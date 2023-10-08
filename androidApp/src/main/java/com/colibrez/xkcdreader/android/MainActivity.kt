package com.colibrez.xkcdreader.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.colibrez.xkcdreader.Greeting
import com.colibrez.xkcdreader.android.repository.ComicsPagingSource
import com.colibrez.xkcdreader.android.repository.ComicsRemoteMediator
import com.colibrez.xkcdreader.data.DriverFactory
import com.colibrez.xkcdreader.data.createDatabase
import com.colibrez.xkcdreader.network.ApiClient
import com.colibrez.xkcdreader.repository.ComicRepository
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val comicQueries = createDatabase(DriverFactory(this)).comicQueries
        MainViewModel.Factory(
            comicsRemoteMediator = ComicsRemoteMediator(
                comicRepository = ComicRepository(
                    comicQueries = comicQueries,
                    ioDispatcher = Dispatchers.IO
                ),
                apiClient = ApiClient(Dispatchers.IO)
            ),
            comicQueries = comicQueries
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val lazyPagingItems = viewModel.pagedComics.collectAsLazyPagingItems()
                    LazyColumn {
                        items(
                            lazyPagingItems.itemCount,
                            key = lazyPagingItems.itemKey { it.num }
                        ) { index ->
                            val item = lazyPagingItems[index] ?: return@items
                            ListItem {
                                Text(text = "${item.num}. ${item.title}")
                            }
                        }
                    }
                }
            }
        }
    }
}
