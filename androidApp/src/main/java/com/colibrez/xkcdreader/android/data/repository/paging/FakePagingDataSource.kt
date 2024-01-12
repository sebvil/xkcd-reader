package com.colibrez.xkcdreader.android.data.repository.paging

import com.colibrez.xkcdreader.android.ui.components.paging.PagingState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePagingDataSource<T> : PagingDataSource<T> {

    val stateValue: MutableStateFlow<PagingState<T>> = MutableStateFlow(
        PagingState(
            items = persistentListOf(),
            status = PagingStatus.Loading,
        ),
    )

    override val state: Flow<PagingState<T>>
        get() = stateValue

    // region fetch

    data class FetchArguments(val pageSize: Long, val isInitialFetch: Boolean)

    val fetchInvocations = mutableListOf<FetchArguments>()

    override suspend fun fetch(pageSize: Long, isInitialFetch: Boolean) {
        fetchInvocations.add(FetchArguments(pageSize, isInitialFetch))
    }

    // endregion
}
