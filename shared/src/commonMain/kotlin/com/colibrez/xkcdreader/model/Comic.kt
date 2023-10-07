package com.colibrez.xkcdreader.model

import kotlinx.serialization.Serializable

@Serializable
data class Comic(
    val num: Long,
    val title: String,
    val transcript: String,
    val img: String,
    val alt: String,
    val link: String,
    val year: Long,
    val month: Long,
    val day: Long,
    val permalink: String = "https://xkcd.com/$num"
)