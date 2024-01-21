package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Stable
class SearchStateHolder(
    viewModelScope: CoroutineScope,
) : StateHolder<SearchState, SearchUserAction> {

    private val queryFlow = MutableStateFlow("")
    private val debouncedQueryFlow = queryFlow.debounce(200.milliseconds)

    override val state: StateFlow<SearchState> =
        viewModelScope.launchMolecule(RecompositionMode.Immediate) {
            Presenter(queryFlow = debouncedQueryFlow)
        }

    override fun handle(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.QuerySubmitted -> queryFlow.update { action.query }
            is SearchUserAction.SearchCleared -> queryFlow.update { "" }
        }
    }

    companion object {

        @Composable
        fun Presenter(
            queryFlow: Flow<String>,
        ): SearchState {
            val query by queryFlow.collectAsState("")
            return SearchState(
                query,
            )
        }
    }
}
