package com.colibrez.xkcdreader.android.ui.features.comic

import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState

sealed interface ComicState : UiState {
    val comicNumber: Long
    val comicTitle: String

    data class Data(
        override val comicNumber: Long,
        override val comicTitle: String,
        val imageUrl: String,
        val altText: String,
        val imageDescription: String,
        val permalink: String,
        val isFavorite: Boolean,
        val showDialog: Boolean
    ) : ComicState

    data class Loading(override val comicNumber: Long, override val comicTitle: String) : ComicState
}
