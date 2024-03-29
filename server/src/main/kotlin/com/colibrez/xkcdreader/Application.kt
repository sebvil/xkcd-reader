package com.colibrez.xkcdreader

import com.colibrez.xkcdreader.database.DriverFactory
import com.colibrez.xkcdreader.database.createDatabase
import com.colibrez.xkcdreader.network.XkcdClient
import com.colibrez.xkcdreader.plugins.configureHTTP
import com.colibrez.xkcdreader.plugins.configureRouting
import com.colibrez.xkcdreader.plugins.configureSecurity
import com.colibrez.xkcdreader.plugins.configureSerialization
import com.colibrez.xkcdreader.plugins.configureTemplating
import com.colibrez.xkcdreader.data.repository.OfflineFirstComicRepository
import com.colibrez.xkcdreader.database.LocalComicDataSource
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val database = createDatabase(DriverFactory())
    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val comicRepository = OfflineFirstComicRepository(LocalComicDataSource(Dispatchers.IO, database))
    val xkcdClient = XkcdClient(Dispatchers.IO)
    init(comicRepository, xkcdClient, applicationScope)
    configureTemplating()
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting(comicRepository)
}
