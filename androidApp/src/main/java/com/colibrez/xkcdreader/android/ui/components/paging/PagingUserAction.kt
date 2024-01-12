package com.colibrez.xkcdreader.android.ui.components.paging

import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction

sealed interface PagingUserAction : UserAction {
    data class FetchPage(val isInitialFetch: Boolean) : PagingUserAction
}
