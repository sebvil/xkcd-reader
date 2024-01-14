package com.colibrez.xkcdreader.network

import com.colibrez.xkcdreader.extensions.getResult
import com.colibrez.xkcdreader.network.model.NetworkComic
import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.http
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient {

    private val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(Resources)

            engine {
                proxy = ProxyBuilder.http("http://localhost:9090")
            }

            defaultRequest {
                url("http://localhost:8080")
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    suspend fun getNewestComics(
        lastFetchTimestamp: Long = 0,
        maxComicNumber: Long = Long.MAX_VALUE,
        limit: Long = Long.MAX_VALUE
    ): Result<List<NetworkComic>> {
        return client.getResult(
            Comics(
                lastFetchTimestamp = lastFetchTimestamp,
                maxComicNumber = maxComicNumber,
                limit = limit
            )
        )
    }

}