package com.colibrez.xkcdreader.android.data.repository.paging

import com.colibrez.xkcdreader.android.ui.components.paging.PagingState
import kotlinx.coroutines.flow.Flow

interface PagingDataSource<T> {
    val state: Flow<PagingState<T>>

    suspend fun fetch(pageSize: Long, isInitialFetch: Boolean)
}