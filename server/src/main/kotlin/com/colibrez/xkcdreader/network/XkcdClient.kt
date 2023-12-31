package com.colibrez.xkcdreader.network

import com.colibrez.xkcdreader.extensions.getResult
import com.colibrez.xkcdreader.model.Comic
import com.colibrez.xkcdreader.network.model.XkcdNetworkComic
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.resources.Resource
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.invoke
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class XkcdClient(private val ioDispatcher: CoroutineDispatcher) {

    @Resource("/info.0.json")
    private object Latest : ApiRoute<XkcdNetworkComic>

    @Resource("{num}/info.0.json")
    private data class XkcdComic(val num: Long) : ApiRoute<XkcdNetworkComic>

    private val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(Resources)

            defaultRequest {
                url("https://xkcd.com")
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    suspend fun getLatest(): Result<XkcdNetworkComic> {
        return withContext(ioDispatcher) {
            client.getResult(Latest)
        }
    }

    suspend fun getComic(num: Long): Result<XkcdNetworkComic> {
        return withContext(ioDispatcher) {
            client.getResult(XkcdComic(num))
        }
    }

    companion object {
        const val MAX_CONNECTIONS_PER_ROUTE = 100
    }

}

