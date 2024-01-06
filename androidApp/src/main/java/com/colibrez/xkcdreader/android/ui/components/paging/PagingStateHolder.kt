package com.colibrez.xkcdreader.android.ui.components.paging

import com.colibrez.xkcdreader.android.data.repository.AllComicsPagingDataSource
import com.colibrez.xkcdreader.android.data.repository.PagingDataSource
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PagingStateHolder<R, T>(
    val pageSize: Long,
    private val viewModelScope: CoroutineScope,
    private val pagingDataSource: PagingDataSource<T>,
    itemTransform: (T) -> R,
) : StateHolder<PagingState<R>, PagingUserAction> {

    override val state: StateFlow<PagingState<R>> = pagingDataSource.state.map {
        PagingState(
            items = it.items.map(itemTransform),
            isLoading = it.isLoading,
            endOfPaginationReached = it.endOfPaginationReached
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PagingState(
            items = listOf(),
            isLoading = false,
            endOfPaginationReached = false
        )
    )

    override fun handle(action: PagingUserAction) {
        when (action) {
            is PagingUserAction.FetchPage -> {
                viewModelScope.launch {
                    pagingDataSource.fetch(pageSize)
                }
            }
        }
    }
}