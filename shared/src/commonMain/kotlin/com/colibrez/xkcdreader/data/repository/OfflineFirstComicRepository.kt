package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.data.model.asExternalModel
import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.database.model.ComicEntityQueries
import com.colibrez.xkcdreader.database.model.ComicInfo
import com.colibrez.xkcdreader.database.model.FavoriteComicEntityQueries
import com.colibrez.xkcdreader.database.model.ReadComicEntityQueries
import com.colibrez.xkcdreader.database.model.UserEntity
import com.colibrez.xkcdreader.database.model.UserEntityQueries
import com.colibrez.xkcdreader.extensions.getList
import com.colibrez.xkcdreader.extensions.getOne
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OfflineFirstComicRepository(
    val comicQueries: ComicEntityQueries,
    val readComicQueries: ReadComicEntityQueries,
    val userEntityQueries: UserEntityQueries,
    val favoriteComicQueries: FavoriteComicEntityQueries,
    private val ioDispatcher: CoroutineDispatcher
) : ComicRepository {

    override fun getComic(num: Long): Flow<Comic> {
        return comicQueries.select(num).getOne(ioDispatcher) { it.asExternalModel()}
    }

    override fun getLatest(): Flow<Comic> {
        return comicQueries.selectLatest().getOne(ioDispatcher) { it.asExternalModel()}
    }

    override fun getCount(): Flow<Long> {
        return comicQueries.count().getOne(ioDispatcher)
    }

    override fun getAllComics(): Flow<List<Comic>> {
        return comicQueries.selectAll().getList(ioDispatcher) { it.asExternalModel() }
    }

    override fun getComicsPaged(next: Long, limit: Long): Flow<List<Comic>> {
        return comicQueries.selectPaged(limit = limit, offset = next).getList(ioDispatcher) { it.asExternalModel() }
    }

    override suspend fun insertComics(comics: List<ComicEntity>) {
        withContext(ioDispatcher) {
            comicQueries.transaction {
                comics.forEach { comic ->
                    comicQueries.insert(comic)
                }
            }
        }
    }

    override suspend fun markAsSeen(comicNum: Long, userId: Long) {
        withContext(ioDispatcher) {
            userEntityQueries.createUser(id = UserEntity.Id(userId))
            readComicQueries.markComicAsRead(comicNum, userId = UserEntity.Id(userId))
        }
    }

    override suspend fun toggleFavorite(comicNum: Long, isFavorite: Boolean, userId: Long) {
        withContext(ioDispatcher) {
            userEntityQueries.createUser(id = UserEntity.Id(userId))
            if (isFavorite) {
                favoriteComicQueries.removeComicFromFavorites(comicNumber = comicNum, userId = UserEntity.Id(userId))
            } else {
                favoriteComicQueries.markComicAsFavorite(comicNum, userId = UserEntity.Id(userId))
            }
        }
    }

    companion object{
        fun mapComicSelecting(
            num: Long,
            title: String,
            transcript: String,
            img: String,
            alt: String,
            link: String,
            year: Long,
            month: Long,
            day: Long,
            isFavorite: Long,
            isRead: Long,
        ): Comic {
            return ComicInfo(
                num = num,
                title = title,
                transcript = transcript,
                img = img,
                alt = alt,
                link = link,
                year = year,
                month = month,
                day = day,
                isFavorite = isFavorite,
                isRead = isRead
            ).asExternalModel()
        }
    }

}