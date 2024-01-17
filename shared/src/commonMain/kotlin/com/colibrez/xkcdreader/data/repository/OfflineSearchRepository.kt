package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.database.SearchDataSource
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.Flow

class OfflineSearchRepository(private val localSearchDataSource: SearchDataSource) :
    SearchRepository {
    override fun searchComics(query: String): Flow<List<Comic>> {
        return localSearchDataSource.searchComics(query)
    }
}