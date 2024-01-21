package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction

sealed interface FilterUserAction : UserAction {
    data class IsReadFilterSelected(val newFilterValue: ReadFilter) : FilterUserAction
    data class IsFavoriteFilterSelected(val newFilterValue: FavoriteFilter) : FilterUserAction
    data class ClearFilter(val filter: Filter<*>) : FilterUserAction
}
