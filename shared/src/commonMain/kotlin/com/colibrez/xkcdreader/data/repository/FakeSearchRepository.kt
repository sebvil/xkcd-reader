package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.model.comicFixtures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSearchRepository : SearchRepository {

    val comics = MutableStateFlow(comicFixtures)
    override fun searchComics(query: String): Flow<List<Comic>> {
        return comics.map {
            if (query.isNotEmpty()) it else listOf()
        }
    }
}