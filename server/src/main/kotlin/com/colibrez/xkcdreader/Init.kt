package com.colibrez.xkcdreader

import com.colibrez.xkcdreader.data.DriverFactory
import com.colibrez.xkcdreader.data.createDatabase
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.XkcdClient
import com.colibrez.xkcdreader.repository.ComicRepository
import io.ktor.server.application.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun Application.init(
    comicRepository: ComicRepository,
    xkcdClient: XkcdClient,
    applicationScope: CoroutineScope
) {
    applicationScope.launch {
        val latestComic = xkcdClient.getLatest()
        val comicNum = latestComic.num
        val savedComics = comicRepository.getAllComics().map { it.map { comic -> comic.num }.toSet()  }.first()
        val comics = mutableListOf<Comic>()
        val mutex = Mutex()
        val jobs = (1..comicNum).mapNotNull {
            if (it !in savedComics) {
                launch {
                    xkcdClient.getComic(it).onSuccess { comic ->
                        mutex.withLock {
                            comics.add(comic)
                        }
                    }
                }
            } else {
                null
            }
        }
        jobs.joinAll()
        comicRepository.insertComics(comics)

    }

}