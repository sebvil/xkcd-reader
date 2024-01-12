package com.colibrez.xkcdreader.android.ui.features.comiclist

data class ListComic(
    val comicNumber: Long,
    val title: String,
    val imageUrl: String,
    val isFavorite: Boolean,
    val isRead: Boolean
)
