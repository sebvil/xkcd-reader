package com.colibrez.xkcdreader.android.ui.features.comiclist.favorites

import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import kotlinx.collections.immutable.ImmutableList

sealed interface FavoritesState : UiState {
    data class Data(val comics: ImmutableList<ListComic>) : FavoritesState
    data object Loading : FavoritesState
}
