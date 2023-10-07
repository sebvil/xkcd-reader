plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
}

group = "com.colibrez.xkcdreader"
version = "0.0.1"

application {
    mainClass.set("com.colibrez.xkcdreader.ApplicationKt")

    val isDevelopment: Boolean = providers.environmentVariable("XKCD_DEVELOPMENT").getOrElse("1") == "1"
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.ktor.server.html.builder.jvm)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.kotlin.css.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.swagger.jvm)
    implementation(libs.ktor.server.http.redirect.jvm)
    implementation(libs.ktor.server.default.headers.jvm)
    implementation(libs.ktor.server.sessions.jvm)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.logback)
    testImplementation(libs.ktor.server.tests.jvm)
    testImplementation(libs.kotlin.test.junit)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.resources)
    implementation(libs.ktor.client.content.negotiation)
}
