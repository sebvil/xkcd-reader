package com.colibrez.xkcdreader.network

import com.colibrez.xkcdreader.network.model.NetworkComic
import io.ktor.resources.Resource


interface ApiRoute<Response>

@Resource("/comics")
data class Comics(val limit: Long = 10, val next: Long = 1): ApiRoute<List<NetworkComic>> {

    @Resource("{num}")
    data class Num(val num: Long, val parent: Comics = Comics()) : ApiRoute<NetworkComic>
}