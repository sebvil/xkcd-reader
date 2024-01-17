package com.colibrez.xkcdreader.android.ui.features.comiclist.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.data.repository.SearchRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@Stable
class SearchStateHolder(
    viewModelScope: CoroutineScope,
    private val searchRepository: SearchRepository
) :
    StateHolder<SearchState, SearchUserAction> {

    private val queryFlow = MutableStateFlow("")

    override val state: StateFlow<SearchState> =
        viewModelScope.launchMolecule(RecompositionMode.Immediate) {
            Presenter(queryFlow = queryFlow, searchRepository = searchRepository)
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
            queryFlow: StateFlow<String>,
            searchRepository: SearchRepository
        ): SearchState {
            val query by queryFlow.collectAsState()
            val results by searchRepository.searchComics(query).collectAsState(initial = listOf())
            return SearchState(
                results = results.map { ListComic.fromExternalModel(it) }
                    .toImmutableList(),
            )
        }
    }
}
