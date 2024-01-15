package com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters

import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState

data class FiltersState(val isReadFilter: EnumFilterState<ReadFilter>) : UiState

data class EnumFilterState<T>(val selection: T) where T : Enum<T>, T : EnumFilter<T>

interface EnumFilter<T : Enum<T>> {
    val displayName: String
}

enum class ReadFilter(override val displayName: String) : EnumFilter<ReadFilter> {
    All("All"),
    Unread("Unread"),
    Read("Read"),
}
