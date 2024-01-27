package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState

data class FiltersState(
    val unread: Filter.Unread,
    val favorites: Filter.Favorites
) : UiState

sealed class Filter(val name: String) {
    abstract val selected: Boolean

    data class Unread(override val selected: Boolean) : Filter(name = "Is unread")
    data class Favorites(override val selected: Boolean) : Filter(name = "Is favorite")
}
