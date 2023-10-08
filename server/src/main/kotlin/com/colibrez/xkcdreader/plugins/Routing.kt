package com.colibrez.xkcdreader.plugins

import com.colibrez.xkcdreader.network.Comics
import com.colibrez.xkcdreader.network.XkcdClient
import com.colibrez.xkcdreader.repository.ComicRepository
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.resources.Resources
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.flow.first

fun Application.configureRouting(comicRepository: ComicRepository) {
    install(Resources)
    routing {
        get<Comics> { params ->
            val comics = comicRepository.getComicsPaged(params.next, params.limit).first()
            call.respond(comics)
        }


        get<Comics.Num> { comic ->
            call.respond(comicRepository.getComic(comic.num).first())
        }
    }
}



