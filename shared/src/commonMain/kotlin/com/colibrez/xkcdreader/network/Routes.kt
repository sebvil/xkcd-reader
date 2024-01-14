package com.colibrez.xkcdreader.network

import com.colibrez.xkcdreader.network.model.NetworkComic
import io.ktor.resources.Resource


interface ApiRoute<Response>

@Resource("/comics")
data class Comics(
    val lastFetchTimestamp: Long = 0,
    val maxComicNumber: Long = Long.MAX_VALUE,
    val limit: Long = Long.MAX_VALUE,
) : ApiRoute<List<NetworkComic>>
