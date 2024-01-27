package com.colibrez.xkcdreader.android.ui.features.comiclist.filters

import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction

sealed interface FilterUserAction : UserAction {
    data class UnreadFilterSelected(val newValue: Boolean) : FilterUserAction
    data class FavoriteFilterSelected(val newValue: Boolean) : FilterUserAction
}
