package com.colibrez.xkcdreader.android.ui.features.favorites

import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction

sealed interface FavoritesUserAction : UserAction {
    data class ToggleFavorite(val comicNum: Long, val isFavorite: Boolean) : FavoritesUserAction
    data class ComicClicked(val comicNum: Long, val comicTitle: String) : FavoritesUserAction
}
