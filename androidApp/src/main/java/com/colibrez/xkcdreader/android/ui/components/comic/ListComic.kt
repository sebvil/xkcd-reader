package com.colibrez.xkcdreader.android.ui.components.comic

import com.colibrez.xkcdreader.model.Comic

data class ListComic(
    val comicNumber: Long,
    val title: String,
    val imageUrl: String,
    val isRead: Boolean
) {

    companion object {
        fun fromExternalModel(comic: Comic) = ListComic(
            comicNumber = comic.number,
            title = comic.title,
            imageUrl = comic.imageUrl,
            isRead = comic.isRead,
        )
    }
}
