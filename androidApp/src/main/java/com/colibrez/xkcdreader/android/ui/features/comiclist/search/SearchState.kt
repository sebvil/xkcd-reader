package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import kotlinx.collections.immutable.ImmutableList

data class SearchState(val results: ImmutableList<ListComic>) : UiState
