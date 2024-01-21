package com.colibrez.xkcdreader.android

import android.content.Context
import com.colibrez.xkcdreader.data.Database
import com.colibrez.xkcdreader.data.repository.OfflineFirstComicRepository
import com.colibrez.xkcdreader.database.DriverFactory
import com.colibrez.xkcdreader.database.LocalComicDataSource
import com.colibrez.xkcdreader.database.createDatabase
import com.colibrez.xkcdreader.network.ApiClient
import kotlinx.coroutines.CoroutineDispatcher

class DependencyContainer(
    private val applicationContext: Context,
    private val ioDispatcher: CoroutineDispatcher
) {

    private val database: Database by lazy {
        createDatabase(DriverFactory(applicationContext))
    }

    val comicRepository by lazy {
        OfflineFirstComicRepository(LocalComicDataSource(ioDispatcher, database))
    }

    val apiClient: ApiClient by lazy {
        ApiClient()
    }
}
