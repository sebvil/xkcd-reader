package com.colibrez.xkcdreader.model

data class Comic(
    val number: Long,
    val title: String,
    val transcript: String,
    val imageUrl: String,
    val altText: String,
    val link: String,
    val year: Long,
    val month: Long,
    val day: Long,
    val isFavorite: Boolean,
    val isRead: Boolean,
) {
    val permalink: String = "https://xkcd.com/$number"
}