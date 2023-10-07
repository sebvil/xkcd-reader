package com.colibrez.xkcdreader

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform