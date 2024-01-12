package com.colibrez.xkcdreader.android.ui.features.comic

import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction

sealed interface ComicUserAction : UserAction {
    data class ToggleFavorite(val comicNum: Long, val isFavorite: Boolean) : ComicUserAction
    data object ImageClicked : ComicUserAction
    data object OverlayClicked : ComicUserAction
    data object BackButtonClicked : ComicUserAction
}
