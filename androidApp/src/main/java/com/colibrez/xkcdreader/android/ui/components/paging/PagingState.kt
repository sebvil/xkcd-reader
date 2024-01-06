package com.colibrez.xkcdreader.android.ui.components.paging

import com.colibrez.xkcdreader.android.data.repository.paging.PagingStatus
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState

data class PagingState<T>(
    val items: List<T>,
    val status: PagingStatus
) : UiState
