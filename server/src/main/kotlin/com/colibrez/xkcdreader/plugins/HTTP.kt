package com.colibrez.xkcdreader.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.httpsredirect.HttpsRedirect
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

fun Application.configureHTTP() {
    routing {
        openAPI(path = "openapi")
    }
    routing {
        swaggerUI(path = "openapi")
    }
//    if (!this.developmentMode) {
//        install(HttpsRedirect) {
//            // The port to redirect to. By default 443, the default HTTPS port.
//            sslPort = 443
//            // 301 Moved Permanently, or 302 Found redirect.
//            permanentRedirect = true
//        }
//    }
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }
}
