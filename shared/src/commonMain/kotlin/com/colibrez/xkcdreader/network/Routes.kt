package com.colibrez.xkcdreader.network

import com.colibrez.xkcdreader.model.Comic
import io.ktor.resources.Resource


interface Route<T>

@Resource("/comics")
data class Comics(val limit: Long = 10, val next: Long = 1): Route<List<Comic>> {

    @Resource("{num}")
    data class Num(val num: Long, val parent: Comics = Comics()) : Route<Comic>
}