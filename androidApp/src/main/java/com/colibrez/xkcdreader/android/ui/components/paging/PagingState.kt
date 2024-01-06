package com.colibrez.xkcdreader.android.ui.components.paging

import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState

data class PagingState<T>(
    val items: List<T>,
    val isLoading: Boolean,
    val endOfPaginationReached: Boolean
) : UiState
