package com.colibrez.xkcdreader.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.data.ComicQueries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ComicRepository(
    private val comicQueries: ComicQueries,
    private val ioDispatcher: CoroutineDispatcher
) {
    fun getComic(num: Long): Flow<Comic> {
        return comicQueries.select(num, ::mapComicSelecting).asFlow().mapToOne(ioDispatcher)
    }

    fun getLatest(): Flow<Comic> {
        return comicQueries.selectLatest(::mapComicSelecting).asFlow().mapToOne(ioDispatcher)
    }

    fun getCount(): Flow<Long> {
        return comicQueries.count().asFlow().mapToOne(ioDispatcher)
    }

    fun getAllComics(): Flow<List<Comic>> {
        return comicQueries.selectAll(::mapComicSelecting).asFlow().mapToList(ioDispatcher)
    }

    fun getComicsPaged(next: Long, limit: Long): Flow<List<Comic>> {
        return comicQueries.selectPaged(next, limit, ::mapComicSelecting).asFlow()
            .mapToList(ioDispatcher)
    }

    suspend fun insertComics(comics: List<Comic>) {
        withContext(ioDispatcher) {
            comicQueries.transaction {
                comics.forEach { comic ->
                    comicQueries.insert(
                        num = comic.num,
                        title = comic.title,
                        transcript = comic.transcript,
                        img = comic.img,
                        alt = comic.alt,
                        link = comic.link,
                        year = comic.year,
                        month = comic.month,
                        day = comic.day
                    )
                }
            }
        }
    }

    private fun mapComicSelecting(
        num: Long,
        title: String,
        transcript: String,
        img: String,
        alt: String,
        link: String,
        year: Long,
        month: Long,
        day: Long,
    ): Comic {
        return Comic(num, title, transcript, img, alt, link, year, month, day)
    }
}