package com.colibrez.xkcdreader.android.ui.components.paging

import com.colibrez.xkcdreader.android.data.repository.paging.PagingStatus
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import kotlinx.collections.immutable.ImmutableList

data class PagingState<T>(
    val items: ImmutableList<T>,
    val status: PagingStatus
) : UiState
