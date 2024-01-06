package com.colibrez.xkcdreader.extensions

import com.colibrez.xkcdreader.network.ApiRoute
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.resources.get
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T> HttpResponse.result(): Result<T> {
    return try {
        Result.success(body<T>())
    } catch (e: NoTransformationFoundException) {
        Result.failure(e)
    } catch (e: HttpRequestTimeoutException) {
        Result.failure(e)
    } catch (e: Exception) {
        println("Exception: $e, $this")
        throw e
    }
}

suspend inline fun <reified T, reified R : ApiRoute<T>> HttpClient.getResult(route: R): Result<T> {
    return try {
        Result.success(get(route).body<T>())
    } catch (e: NoTransformationFoundException) {
        Result.failure(e)
    } catch (e: HttpRequestTimeoutException) {
        Result.failure(e)
    } catch (e: Exception) {
        println("Exception: $e, $this")
        throw e
    }
}