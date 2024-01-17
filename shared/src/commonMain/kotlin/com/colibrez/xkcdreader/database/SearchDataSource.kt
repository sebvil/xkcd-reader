package com.colibrez.xkcdreader.database

import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.Flow

interface SearchDataSource {
    fun searchComics(query: String): Flow<List<Comic>>
}