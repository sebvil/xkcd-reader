package com.colibrez.xkcdreader.database

import app.cash.sqldelight.Query
import com.colibrez.xkcdreader.data.Database
import com.colibrez.xkcdreader.data.model.asExternalModel
import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.database.model.ComicInfo
import com.colibrez.xkcdreader.database.model.UserEntity
import com.colibrez.xkcdreader.extensions.getList
import com.colibrez.xkcdreader.extensions.getOne
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class LocalComicDataSource(
    private val ioDispatcher: CoroutineDispatcher,
    private val database: Database
) : ComicDataSource {
    override fun getComic(num: Long): Flow<Comic> {
        return database.comicEntityQueries.select(num).asExternalModelFlow()
    }

    override fun getLatest(): Flow<Comic> {
        return database.comicEntityQueries.selectLatest().asExternalModelFlow()
    }

    override fun getComicCount(): Flow<Long> {
        return database.comicEntityQueries.count().getOne(ioDispatcher)
    }

    override fun getAllComics(isRead: Boolean?, isFavorite: Boolean?): Flow<List<Comic>> {
        return database.comicEntityQueries.selectAll(isRead = isRead, isFavorite = isFavorite)
            .asExternalModelsFlow()
    }


    override fun getNewestComics(
        lastFetchTimestamp: Long,
        maxComicNumber: Long,
        limit: Long
    ): Flow<List<Comic>> {
        return database.comicEntityQueries.getNewComics(
            lastFetchTimestamp = lastFetchTimestamp,
            maxComicNumber = maxComicNumber,
            limit = limit
        ).asExternalModelsFlow()
    }

    override suspend fun insertComics(comics: List<ComicEntity>) {
        withContext(ioDispatcher) {
            database.comicEntityQueries.transaction {
                comics.forEach { comic ->
                    database.comicEntityQueries.insert(comic)
                }
            }
        }
    }

    override suspend fun markAsSeen(comicNum: Long, userId: Long) {
        withContext(ioDispatcher) {
            database.userEntityQueries.createUser(id = UserEntity.Id(userId))
            database.readComicEntityQueries.markComicAsRead(
                comicNum,
                userId = UserEntity.Id(userId)
            )
        }
    }

    override suspend fun toggleFavorite(comicNum: Long, isFavorite: Boolean, userId: Long) {
        withContext(ioDispatcher) {
            database.userEntityQueries.createUser(id = UserEntity.Id(userId))
            if (isFavorite) {
                database.favoriteComicEntityQueries.removeComicFromFavorites(
                    comicNumber = comicNum,
                    userId = UserEntity.Id(userId)
                )
            } else {
                database.favoriteComicEntityQueries.markComicAsFavorite(
                    comicNum,
                    userId = UserEntity.Id(userId)
                )
            }
        }
    }

    override suspend fun getLatestUpdateTimestamp(): Long {
        return database.comicEntityQueries.getLatestTimestamp()
            .getOne(ioDispatcher) { it.latestTimestamp }.firstOrNull() ?: 0
    }


    private fun Query<ComicInfo>.asExternalModelsFlow(): Flow<List<Comic>> =
        getList(ioDispatcher) { it.asExternalModel() }

    private fun Query<ComicInfo>.asExternalModelFlow(): Flow<Comic> =
        getOne(ioDispatcher) { it.asExternalModel() }

}