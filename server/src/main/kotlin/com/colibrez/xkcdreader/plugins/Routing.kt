package com.colibrez.xkcdreader.plugins

import com.colibrez.xkcdreader.data.model.asNetworkComic
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.network.ApiRoute
import com.colibrez.xkcdreader.network.Comics
import com.colibrez.xkcdreader.network.Latest
import com.colibrez.xkcdreader.network.model.NetworkComic
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.resources.Resources
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

fun Application.configureRouting(comicRepository: ComicRepository) {
    install(Resources)
    routing {
        get<List<NetworkComic>, Comics> { params ->
            comicRepository.getComicsPaged(params.next, params.limit).first()
                .map { it.asNetworkComic() }
        }

        get<NetworkComic, Comics.Num> { comic ->
            comicRepository.getComic(comic.num).first().asNetworkComic()
        }

        get<NetworkComic, Latest> {
            // TODO separate network and local repository
            comicRepository.getAllComics().map { it.last() }.first().asNetworkComic()
        }
    }
}

inline fun <reified Response : Any, reified T : ApiRoute<Response>> Route.get(
    noinline body: suspend PipelineContext<Unit, ApplicationCall>.(T) -> Response
): Route {
    return get<T> { params ->
        val result = body(params)
        call.respond(result)
    }
}