package com.colibrez.xkcdreader.android.ui.features.latest

import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState

sealed interface LatestComicState : UiState {

    data class Data(
        val comicNumber: Long,
        val comicTitle: String,
        val imageUrl: String,
        val altText: String,
        val imageDescription: String,
        val permalink: String,
        val isFavorite: Boolean,
        val showDialog: Boolean
    ) : LatestComicState

    data object Loading : LatestComicState
}
