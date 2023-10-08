package com.colibrez.xkcdreader.network

import com.colibrez.xkcdreader.extensions.getResult
import com.colibrez.xkcdreader.model.Comic
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.statement.HttpResponse
import io.ktor.resources.Resource
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class XkcdClient(private val ioDispatcher: CoroutineDispatcher) {

    @Resource("/info.0.json")
    private object Latest : Route<Comic>

    @Resource("{num}/info.0.json")
    private data class XkcdComic(val num: Long) : Route<Comic>

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

    suspend fun getLatest(): Result<Comic> {
        return withContext(ioDispatcher) {
            client.getResult(Latest)
        }
    }

    suspend fun getComic(num: Long): Result<Comic> {
        return withContext(ioDispatcher) {
            client.getResult(XkcdComic(num))
        }
    }

}

