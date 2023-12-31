package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.Flow

interface ComicRepository {
    fun getComic(num: Long): Flow<Comic>
    fun getLatest(): Flow<Comic>
    fun getCount(): Flow<Long>
    fun getAllComics(): Flow<List<Comic>>
    fun getComicsPaged(next: Long, limit: Long): Flow<List<Comic>>

    suspend fun insertComics(comics: List<ComicEntity>)

    suspend fun markAsSeen(comicNum: Long, userId: Long = 0L)

    suspend fun toggleFavorite(comicNum: Long, isFavorite: Boolean, userId: Long = 0L)
}