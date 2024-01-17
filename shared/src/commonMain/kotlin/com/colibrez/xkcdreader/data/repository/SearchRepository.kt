package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchComics(query: String): Flow<List<Comic>>
}