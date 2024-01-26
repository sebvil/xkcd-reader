package com.colibrez.xkcdreader.android.ui.features.comic

import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState

sealed interface ComicState : UiState {
    val comicNumber: Long?

    data class Data(
        override val comicNumber: Long,
        val comicTitle: String,
        val imageUrl: String,
        val altText: String,
        val imageDescription: String,
        val permalink: String,
        val explainXckdPermalink: String,
        val isFavorite: Boolean,
        val showDialog: Boolean,
        val nextComic: Long?,
        val previousComic: Long?,
        val firstComic: Long?,
        val lastComic: Long?
    ) : ComicState

    data class Loading(override val comicNumber: Long?) : ComicState
}
