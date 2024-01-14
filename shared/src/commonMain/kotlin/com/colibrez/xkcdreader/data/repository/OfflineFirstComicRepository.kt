package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.data.model.asEntity
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.database.LocalComicDataSource
import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.network.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

class OfflineFirstComicRepository(
    private val localComicDataSource: LocalComicDataSource,
    private val apiClient: ApiClient,
) : ComicRepository {

    override fun getComic(num: Long): Flow<Comic> {
        return localComicDataSource.getComic(num = num)
    }

    override fun getLatest(): Flow<Comic> {
        return flow {
            // TODO emit loading value
            apiClient.getLatest().onSuccess {
                insertComics(listOf(it.asEntity()))
            }
            emitAll(localComicDataSource.getLatest())
        }
    }

    override fun getComicCount(): Flow<Long> {
        return localComicDataSource.getComicCount()
    }

    override fun getAllComics(): Flow<List<Comic>> {
        return localComicDataSource.getAllComics()
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

    override suspend fun latestUpdateTimestamp(): Long {
        return 0
    }

}