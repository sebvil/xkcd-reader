package com.colibrez.xkcdreader.database

import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.Flow

interface LocalComicDataSource {
    fun getComic(num: Long): Flow<Comic>
    fun getLatest(): Flow<Comic>
    fun getAllComics(): Flow<List<Comic>>
    fun getComicCount(): Flow<Long>
    fun getComicsPaged(next: Long, limit: Long): Flow<List<Comic>>

    suspend fun insertComics(comics: List<ComicEntity>)

    suspend fun markAsSeen(comicNum: Long, userId: Long = 0L)

    suspend fun toggleFavorite(comicNum: Long, isFavorite: Boolean, userId: Long = 0L)
}