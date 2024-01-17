package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction

sealed interface SearchUserAction : UserAction {
    data class QuerySubmitted(val query: String) : SearchUserAction
    data object SearchCleared : SearchUserAction
}
