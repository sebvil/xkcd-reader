package com.colibrez.xkcdreader.network.model

import kotlinx.serialization.Serializable

/**
 * Data representation of comics sent down by the xkcd API
 */
@Serializable
data class XkcdNetworkComic(
    val num: Long,
    val title: String,
    val transcript: String,
    val img: String,
    val alt: String,
    val link: String,
    val year: Long,
    val month: Long,
    val day: Long,
)
