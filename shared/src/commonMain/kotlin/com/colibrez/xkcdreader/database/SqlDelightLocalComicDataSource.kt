package com.colibrez.xkcdreader.database

import com.colibrez.xkcdreader.data.Database
import com.colibrez.xkcdreader.data.model.asExternalModel
import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.database.model.UserEntity
import com.colibrez.xkcdreader.extensions.getList
import com.colibrez.xkcdreader.extensions.getOne
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SqlDelightLocalComicDataSource(
    private val ioDispatcher: CoroutineDispatcher,
    private val database: Database
) : LocalComicDataSource {
    override fun getComic(num: Long): Flow<Comic> {
        return database.comicEntityQueries.select(num).getOne(ioDispatcher) { it.asExternalModel() }
    }

    override fun getLatest(): Flow<Comic> {
        return database.comicEntityQueries.selectLatest()
            .getOne(ioDispatcher) { it.asExternalModel() }
    }

    override fun getComicCount(): Flow<Long> {
        return database.comicEntityQueries.count().getOne(ioDispatcher)
    }

    override fun getAllComics(): Flow<List<Comic>> {
        return database.comicEntityQueries.selectAll().getList(ioDispatcher) { it.asExternalModel() }
    }

    override fun getComicsPaged(next: Long, limit: Long): Flow<List<Comic>> {
        return database.comicEntityQueries.selectPaged(limit = limit, offset = next)
            .getList(ioDispatcher) { it.asExternalModel() }
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

}