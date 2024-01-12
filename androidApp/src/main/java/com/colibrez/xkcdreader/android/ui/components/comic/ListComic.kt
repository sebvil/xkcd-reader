package com.colibrez.xkcdreader.android.ui.components.comic

import com.colibrez.xkcdreader.model.Comic

data class ListComic(
    val comicNumber: Long,
    val title: String,
    val imageUrl: String,
    val isFavorite: Boolean,
    val isRead: Boolean
) {

    companion object {
        fun fromExternalModel(comic: Comic) = ListComic(
            comicNumber = comic.number,
            title = comic.title,
            imageUrl = comic.imageUrl,
            isFavorite = comic.isFavorite,
            isRead = comic.isRead,
        )
    }
}
