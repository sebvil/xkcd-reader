package com.colibrez.xkcdreader.android.ui.components.paging

import com.colibrez.xkcdreader.android.data.repository.paging.PagingDataSource
import com.colibrez.xkcdreader.android.data.repository.paging.PagingStatus
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

    // Helps preserve scroll position in the case we navigate to another screen
    private var cachedItems: List<R> = listOf()

    override val state: StateFlow<PagingState<R>> = pagingDataSource.state.map {
        if (it.items.isNotEmpty()) {
            cachedItems = it.items.map(itemTransform)
        }
        PagingState(
            items = cachedItems,
            status = it.status
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PagingState(
            items = cachedItems,
            status = PagingStatus.Loading
        )
    )

    override fun handle(action: PagingUserAction) {
        when (action) {
            is PagingUserAction.FetchPage -> {
                viewModelScope.launch {
                    pagingDataSource.fetch(pageSize, action.isInitialFetch)
                }
            }
        }
    }
}