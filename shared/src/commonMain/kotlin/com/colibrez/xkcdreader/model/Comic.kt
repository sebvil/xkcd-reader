package com.colibrez.xkcdreader.model

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
    val isFavorite: Boolean,
    val isRead: Boolean,
) {
    val permalink: String = "https://xkcd.com/$num"
}