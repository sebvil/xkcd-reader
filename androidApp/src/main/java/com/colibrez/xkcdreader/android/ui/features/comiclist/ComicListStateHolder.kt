package com.colibrez.xkcdreader.android.ui.features.comiclist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.ui.components.comic.ListComic
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.features.comic.ComicArguments
import com.colibrez.xkcdreader.data.repository.ComicRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ComicListStateHolder(
    private val props: StateFlow<ComicListProps>,
    private val viewModelScope: CoroutineScope,
    private val comicRepository: ComicRepository,
    private val delegate: ComicListDelegate,
) : StateHolder<ComicListState, ComicListUserAction> {

    override val state: StateFlow<ComicListState> by lazy {
        viewModelScope.launchMolecule(RecompositionMode.Immediate) {
            Presenter(
                propsFlow = props,
                comicRepository = comicRepository,
            )
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
                delegate.showComic(
                    ComicArguments(
                        comicNumber = action.comicNum,
                        comicTitle = action.comicTitle,
                    ),
                )
            }
        }
    }

    companion object {
        @Composable
        private fun Presenter(
            propsFlow: StateFlow<ComicListProps>,
            comicRepository: ComicRepository
        ): ComicListState {
            val props by propsFlow.collectAsState()

            val comics by remember(props) {
                flow {
                    emit(null)
                    emitAll(
                        comicRepository.getAllComics(
                            isUnread = props.isUnreadFilterApplied,
                            isFavorite = props.isFavoriteFilterApplied,
                            searchQuery = props.searchQuery,
                        ),
                    )
                }
            }.collectAsState(initial = null)

            return comics?.let {
                ComicListState.Data(
                    comics = it.map(ListComic::fromExternalModel)
                        .toImmutableList(),
                )
            } ?: ComicListState.Loading
        }
    }
}
