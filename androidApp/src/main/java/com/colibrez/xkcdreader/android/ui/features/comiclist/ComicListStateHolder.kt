package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseStateHolder
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicScreenArguments
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FavoriteFilter
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FilterUserAction
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.FiltersState
import com.colibrez.xkcdreader.android.ui.features.comiclist.filters.ReadFilter
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ComicListStateHolder(
    private val viewModelScope: CoroutineScope,
    filterStateHolder: StateHolder<FiltersState, FilterUserAction>,
    private val comicRepository: ComicRepository,
) : BaseStateHolder<ComicListState, ComicListUserAction>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val comicsFlow: Flow<List<Comic>?> =
        filterStateHolder.state.flatMapLatest { filterState ->
            flow {
                // This forces the loading screen to show up after a filter change
                emit(null)
                emitAll(
                    comicRepository.getAllComics(
                        isRead = when (filterState.isReadFilter.selection) {
                            ReadFilter.All -> null
                            ReadFilter.Unread -> false
                            ReadFilter.Read -> true
                        },
                        isFavorite = when (filterState.favoriteFilter.selection) {
                            FavoriteFilter.All -> null
                            FavoriteFilter.Favorites -> true
                        },
                    ),
                )
            }
        }

    override val state: StateFlow<ComicListState> by lazy {
        viewModelScope.launchMolecule(RecompositionMode.Immediate) {
            Presenter(comicsFlow = comicsFlow)
        }
    }

    override fun handle(action: ComicListUserAction) {
        when (action) {
            is ComicListUserAction.ToggleFavorite -> {
                viewModelScope.launch {
                    comicRepository.toggleFavorite(action.comicNum, action.isFavorite)
                }
            }

            is ComicListUserAction.ComicClicked -> {
                setNavigationState(
                    NavigationState.ShowScreen(
                        ComicScreenArguments(
                            comicNumber = action.comicNum,
                            comicTitle = action.comicTitle,
                        ),
                    ),
                )
            }
        }
    }

    companion object {
        @Composable
        private fun Presenter(comicsFlow: Flow<List<Comic>?>): ComicListState {
            val comics by comicsFlow.collectAsState(initial = null)

            return comics?.let {
                ComicListState.Data(
                    comics = it.map(ListComic::fromExternalModel)
                        .toImmutableList(),
                )
            } ?: ComicListState.Loading
        }
    }
}
