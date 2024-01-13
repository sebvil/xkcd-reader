package com.colibrez.xkcdreader.android.ui.components.paging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.data.repository.paging.PagingDataSource
import com.colibrez.xkcdreader.android.data.repository.paging.PagingStatus
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PagingStateHolder<R, T>(
    val pageSize: Long,
    private val viewModelScope: CoroutineScope,
    private val pagingDataSource: PagingDataSource<T>,
    itemTransform: (T) -> R,
) : StateHolder<PagingState<R>, PagingUserAction> {

    // Helps preserve scroll position in the case we navigate to another screen
    private var _cachedItems: ImmutableList<R> = persistentListOf()
    private val _pagingDataSourceStateFlow = pagingDataSource.state

    override val state: StateFlow<PagingState<R>> =
        viewModelScope.launchMolecule(RecompositionMode.Immediate) {
            Presenter(
                pagingDataSourceStateFlow = _pagingDataSourceStateFlow,
                cachedItems = _cachedItems,
                cacheItems = { _cachedItems = it },
                itemTransform = itemTransform,
            )
        }

    override fun handle(action: PagingUserAction) {
        when (action) {
            is PagingUserAction.FetchPage -> {
                viewModelScope.launch {
                    pagingDataSource.fetch(pageSize, action.isInitialFetch)
                }
            }
        }
    }

    @Composable
    fun Presenter(
        pagingDataSourceStateFlow: Flow<PagingState<T>>,
        cachedItems: ImmutableList<R>,
        cacheItems: (ImmutableList<R>) -> Unit,
        itemTransform: (T) -> R,
    ): PagingState<R> {
        val pagingDataSourceState by pagingDataSourceStateFlow.collectAsState(initial = null)

        return pagingDataSourceState?.let {
            val newItems = if (it.items.isNotEmpty()) {
                it.items.map(itemTransform).toImmutableList().also(cacheItems)
            } else {
                cachedItems
            }
            PagingState(
                items = newItems,
                status = it.status,
            )
        } ?: PagingState(
            items = cachedItems,
            status = PagingStatus.Loading,
        )
    }
}
