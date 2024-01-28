package com.colibrez.xkcdreader.android.ui.features.comic

import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState

sealed interface ComicState : UiState {
    val comicNumber: Long?
    val navigationState: NavigationState?

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
        override val navigationState: NavigationState?
    ) : ComicState

    data class Loading(
        override val comicNumber: Long?,
        override val navigationState: NavigationState?
    ) : ComicState
}

data class NavigationState(
    val currentComic: Long?,
    val nextComic: Long?,
    val previousComic: Long?,
    val firstComic: Long?,
    val lastComic: Long?
) {

    companion object {
        fun fromComicAndShownComics(comicNumber: Long?, shownComics: List<Long>): NavigationState {
            return NavigationState(
                currentComic = comicNumber,
                nextComic = comicNumber?.let {
                    shownComics.getOrNull(shownComics.indexOf(it) + 1)
                },
                previousComic = comicNumber?.let {
                    shownComics.getOrNull(shownComics.indexOf(it) - 1)
                },
                firstComic = shownComics.firstOrNull(),
                lastComic = shownComics.lastOrNull(),
            )
        }
    }
}
