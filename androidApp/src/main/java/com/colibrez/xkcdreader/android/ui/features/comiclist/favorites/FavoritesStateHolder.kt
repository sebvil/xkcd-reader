package com.colibrez.xkcdreader.android.ui.features.comiclist.favorites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.features.comiclist.ComicListStateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class FavoritesStateHolder(
    viewModelScope: CoroutineScope,
    comicRepository: ComicRepository,
) : ComicListStateHolder<FavoritesState>(viewModelScope, comicRepository) {

    private val _favorites = comicRepository.getFavorites()
    override val state: StateFlow<FavoritesState> =
        viewModelScope.launchMolecule(RecompositionMode.Immediate) {
            Presenter(favoritesFlow = _favorites)
        }

    @Composable
    private fun Presenter(favoritesFlow: Flow<List<Comic>>): FavoritesState {
        val favorites by favoritesFlow.collectAsState(initial = null)

        return favorites?.let { comics ->
            FavoritesState.Data(
                comics = comics.map { ListComic.fromExternalModel(it) }
                    .toImmutableList(),
            )
        } ?: FavoritesState.Loading
    }
}
