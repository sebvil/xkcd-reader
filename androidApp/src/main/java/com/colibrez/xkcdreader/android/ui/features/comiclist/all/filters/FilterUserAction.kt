package com.colibrez.xkcdreader.android.ui.features.comiclist.all.filters

import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction

sealed interface FilterUserAction : UserAction {
    data class IsReadFilterSelected(val newFilterValue: ReadFilter) : FilterUserAction
}

