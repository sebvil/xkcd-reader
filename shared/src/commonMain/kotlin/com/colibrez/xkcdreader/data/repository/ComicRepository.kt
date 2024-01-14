package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.Flow

interface ComicRepository {
    fun getComic(num: Long): Flow<Comic>
    fun getLatest(): Flow<Comic>
    fun getComicCount(): Flow<Long>
    fun getAllComics(isRead: Boolean?): Flow<List<Comic>>
    fun getNewestComics(
        lastFetchTimestamp: Long = 0,
        maxComicNumber: Long = Long.MAX_VALUE,
        limit: Long = Long.MAX_VALUE
    ): Flow<List<Comic>>

    fun getFavorites(): Flow<List<Comic>>

    suspend fun insertComics(comics: List<ComicEntity>)

    suspend fun markAsSeen(comicNum: Long, userId: Long = 0L)

    suspend fun toggleFavorite(comicNum: Long, isFavorite: Boolean, userId: Long = 0L)

    suspend fun getLatestUpdateTimestamp(): Long
}