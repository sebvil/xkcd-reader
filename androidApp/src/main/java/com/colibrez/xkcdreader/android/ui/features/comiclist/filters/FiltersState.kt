package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState

data class FiltersState(
    val isReadFilter: Filter.IsRead,
    val favoriteFilter: Filter.Favorite
) : UiState

sealed class Filter<T>(val name: String, val default: T) where T : Enum<T>, T : EnumFilter<T> {
    abstract val selection: T

    data class IsRead(override val selection: ReadFilter) :
        Filter<ReadFilter>(name = "Is read", default = ReadFilter.All)

    data class Favorite(override val selection: FavoriteFilter) :
        Filter<FavoriteFilter>(name = "Favorite", default = FavoriteFilter.All)
}

interface EnumFilter<T : Enum<T>> {
    val displayName: String
}

enum class ReadFilter(override val displayName: String) : EnumFilter<ReadFilter> {
    All("All"),
    Unread("Unread"),
    Read("Read"),
}

enum class FavoriteFilter(override val displayName: String) : EnumFilter<FavoriteFilter> {
    All("All"),
    Favorites("Favorites"),
}
