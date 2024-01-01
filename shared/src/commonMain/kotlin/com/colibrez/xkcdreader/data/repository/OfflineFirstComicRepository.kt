package com.colibrez.xkcdreader.data.repository

import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.data.model.asExternalModel
import com.colibrez.xkcdreader.database.LocalComicDataSource
import com.colibrez.xkcdreader.database.model.ComicEntity
import com.colibrez.xkcdreader.database.model.ComicInfo
import kotlinx.coroutines.flow.Flow

class OfflineFirstComicRepository(
    private val localComicDataSource: LocalComicDataSource,
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

    override fun getAllComics(): Flow<List<Comic>> {
        return localComicDataSource.getAllComics()
    }

    override fun getComicsPaged(next: Long, limit: Long): Flow<List<Comic>> {
        return localComicDataSource.getComicsPaged(next = next, limit = limit)
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

    companion object {
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