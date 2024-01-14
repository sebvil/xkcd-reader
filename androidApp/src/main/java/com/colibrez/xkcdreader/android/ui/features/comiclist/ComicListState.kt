package com.colibrez.xkcdreader.android.ui.features.comiclist

import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import kotlinx.collections.immutable.ImmutableList

sealed interface ComicListState : UiState {
    data class Data(val comics: ImmutableList<ListComic>) : ComicListState
    data object Loading : ComicListState
}
