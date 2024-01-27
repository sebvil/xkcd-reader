package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import com.colibrez.xkcdreader.android.ui.core.mvvm.NoState
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Stable
class SearchStateHolder(
    delegate: SearchDelegate,
    stateHolderScope: CoroutineScope,
) : StateHolder<NoState, SearchUserAction> {

    private val queryFlow = MutableStateFlow("")
    private val debouncedQueryFlow = queryFlow.debounce(200.milliseconds)

    override val state: StateFlow<NoState> = MutableStateFlow(NoState).asStateFlow()

    init {
        debouncedQueryFlow.onEach {
            delegate.onSearchQueryUpdated(newQuery = it)
        }.launchIn(stateHolderScope)
    }

    override fun handle(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.QuerySubmitted -> queryFlow.update { action.query }
            is SearchUserAction.SearchCleared -> queryFlow.update { "" }
        }
    }
}
