package com.colibrez.xkcdreader.android.ui.features.latest

import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction

sealed interface LatestComicUserAction : UserAction {
    data class ToggleFavorite(val comicNum: Long, val isFavorite: Boolean) : LatestComicUserAction
    data object ImageClicked : LatestComicUserAction
    data object OverlayClicked : LatestComicUserAction
}
