package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.database.ComicDataSource
import com.colibrez.xkcdreader.database.model.ComicEntity
import kotlinx.coroutines.flow.Flow

class OfflineFirstComicRepository(
    private val localComicDataSource: ComicDataSource,
) : ComicRepository {

    override fun getComic(num: Long): Flow<Comic> {
        return localComicDataSource.getComic(num = num)
    }

    override fun getLatest(): Flow<Comic> {
        return localComicDataSource.getLatest()
    }

    override fun getComicCount(): Flow<Long> {
        return localComicDataSource.getComicCount()
    }

    override fun getAllComics(isRead: Boolean?): Flow<List<Comic>> {
        return localComicDataSource.getAllComics(isRead = isRead)
    }

    override fun getNewestComics(
        lastFetchTimestamp: Long,
        maxComicNumber: Long,
        limit: Long
    ): Flow<List<Comic>> {
        return localComicDataSource.getNewestComics(
            lastFetchTimestamp = lastFetchTimestamp,
            maxComicNumber = maxComicNumber,
            limit = limit
        )
    }

    override fun getFavorites(): Flow<List<Comic>> {
        return localComicDataSource.getFavorites()
    }

    override suspend fun insertComics(comics: List<ComicEntity>) {
        localComicDataSource.insertComics(comics = comics)
    }

    override suspend fun markAsSeen(comicNum: Long, userId: Long) {
        localComicDataSource.markAsSeen(comicNum = comicNum, userId = userId)
    }

    override suspend fun toggleFavorite(comicNum: Long, isFavorite: Boolean, userId: Long) {
        localComicDataSource.toggleFavorite(
            comicNum = comicNum,
            isFavorite = isFavorite,
            userId = userId
        )
    }

    override suspend fun getLatestUpdateTimestamp(): Long {
        return localComicDataSource.getLatestUpdateTimestamp()
    }

}