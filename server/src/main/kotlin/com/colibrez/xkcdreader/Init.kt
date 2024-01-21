package com.colibrez.xkcdreader

import com.colibrez.xkcdreader.data.model.asEntity
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.network.XkcdClient
import io.ktor.server.application.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun Application.init(
    comicRepository: ComicRepository,
    xkcdClient: XkcdClient,
    applicationScope: CoroutineScope
) {
    applicationScope.launch {
        val latestComic = xkcdClient.getLatest().getOrElse { return@launch }
        val comicNum = latestComic.num
        val savedComics =
            comicRepository.getAllComics(isRead = null, isFavorite = null, searchQuery = "")
                .map { it.map { comic -> comic.number }.toSet() }.first()
        val comics = (1..comicNum).filter { it !in savedComics && it != 404L }
            .chunked(XkcdClient.MAX_CONNECTIONS_PER_ROUTE)
            .flatMap { chunk ->
                chunk.map { comicNum ->
                    async {
                        xkcdClient.getComic(comicNum).getOrElse { null }
                    }
                }.awaitAll().filterNotNull()
            }
        comicRepository.insertComics(comics.map { it.asEntity() })
    }

}