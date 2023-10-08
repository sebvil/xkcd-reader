package com.colibrez.xkcdreader.network

import com.colibrez.xkcdreader.extensions.getResult
import com.colibrez.xkcdreader.model.Comic
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ApiClient(private val ioDispatcher: CoroutineDispatcher) {

    private val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(Resources)

            defaultRequest {
                // Run the following command to connect to the server from Android device
                // adb reverse tcp:8080 tcp:8080
                url("http://localhost:8080")
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    suspend fun getPaginatedComics(next: Long = 1, limit: Long = 10): Result<List<Comic>> {
        return withContext(ioDispatcher) {
            client.getResult(Comics(limit = limit, next = next))
        }
    }

    suspend fun getComic(num: Long): Result<Comic> {
        return withContext(ioDispatcher) {
            client.getResult(Comics.Num(num = num))
        }
    }

}