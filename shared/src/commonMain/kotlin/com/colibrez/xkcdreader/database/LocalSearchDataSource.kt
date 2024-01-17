package com.colibrez.xkcdreader.database

import com.colibrez.xkcdreader.data.Database
import com.colibrez.xkcdreader.data.model.asExternalModel
import com.colibrez.xkcdreader.extensions.getList
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class LocalSearchDataSource(
    private val ioDispatcher: CoroutineDispatcher,
    private val database: Database
) : SearchDataSource {
    override fun searchComics(query: String): Flow<List<Comic>> {
        return database.comicSearchQueries.search("\"$query*\"")
            .getList(ioDispatcher) { it.asExternalModel() }
    }
}