package com.colibrez.xkcdreader.android.ui.features.comiclist

import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction

sealed interface ComicListUserAction : UserAction {
    data class ToggleFavorite(val comicNum: Long, val isFavorite: Boolean) : ComicListUserAction
    data class ComicClicked(
        val comicNum: Long,
        val comicTitle: String,
        val shownComics: List<Long>
    ) : ComicListUserAction
}
