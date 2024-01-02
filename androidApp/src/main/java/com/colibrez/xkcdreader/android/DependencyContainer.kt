package com.colibrez.xkcdreader.android

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import app.cash.sqldelight.paging3.QueryPagingSource
import com.colibrez.xkcdreader.android.data.repository.ComicsRemoteMediator
import com.colibrez.xkcdreader.data.Database
import com.colibrez.xkcdreader.data.repository.OfflineFirstComicRepository
import com.colibrez.xkcdreader.database.DriverFactory
import com.colibrez.xkcdreader.database.SqlDelightLocalComicDataSource
import com.colibrez.xkcdreader.database.createDatabase
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.ApiClient
import kotlinx.coroutines.Dispatchers

class DependencyContainer(private val applicationContext: Context) {

    private val database: Database by lazy {
        createDatabase(DriverFactory(applicationContext))
    }

    val comicRepository by lazy {
        OfflineFirstComicRepository(SqlDelightLocalComicDataSource(Dispatchers.IO, database))
    }

    val comicPagingSourceFactory: () -> PagingSource<Int, Comic> = {
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
        )
    }

    val apiClient: ApiClient by lazy {
        ApiClient(Dispatchers.IO)
    }

    @OptIn(ExperimentalPagingApi::class)
    val comicsRemoteMediator: RemoteMediator<Int, Comic> by lazy {
        ComicsRemoteMediator(comicRepository, apiClient)
    }

}