package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.data.model.asEntity
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.data.model.asExternalModel
import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.database.model.ComicEntityQueries
import com.colibrez.xkcdreader.database.model.ComicInfo
import com.colibrez.xkcdreader.database.model.ReadComicEntity
import com.colibrez.xkcdreader.database.model.ReadComicEntityQueries
import com.colibrez.xkcdreader.database.model.UserEntity
import com.colibrez.xkcdreader.database.model.UserEntityQueries
import com.colibrez.xkcdreader.extensions.getList
import com.colibrez.xkcdreader.extensions.getOne
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ComicRepository(
    val comicQueries: ComicEntityQueries,
    val readComicsQueries: ReadComicEntityQueries,
    val userEntityQueries: UserEntityQueries,
    private val ioDispatcher: CoroutineDispatcher
) {

    fun getComic(num: Long): Flow<Comic> {
        return comicQueries.select(num).getOne(ioDispatcher) { it.asExternalModel()}
    }

    fun getLatest(): Flow<Comic> {
        return comicQueries.selectLatest().getOne(ioDispatcher) { it.asExternalModel()}
    }

    fun getCount(): Flow<Long> {
        return comicQueries.count().getOne(ioDispatcher)
    }

    fun getAllComics(): Flow<List<Comic>> {
        return comicQueries.selectAll().getList(ioDispatcher) { it.asExternalModel() }
    }

    fun getComicsPaged(next: Long, limit: Long): Flow<List<Comic>> {
        return comicQueries.selectPaged(limit = limit, offset = next).getList(ioDispatcher) { it.asExternalModel() }
    }

    suspend fun insertComics(comics: List<ComicEntity>) {
        withContext(ioDispatcher) {
            comicQueries.transaction {
                comics.forEach { comic ->
                    comicQueries.insert(comic)
                }
            }
        }
    }

    suspend fun markAsSeen(comicNum: Long, userId: Long = 0L) {
        withContext(ioDispatcher) {
            userEntityQueries.createUser(id = UserEntity.Id(userId))
            readComicsQueries.markComicAsRead(comicNum, userId = UserEntity.Id(userId))
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