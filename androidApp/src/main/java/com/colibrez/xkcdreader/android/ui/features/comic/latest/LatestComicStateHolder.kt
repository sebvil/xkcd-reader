package com.colibrez.xkcdreader.android.ui.features.comic.latest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.features.comic.ComicState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicUserAction
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LatestComicStateHolder(
    private val viewModelScope: CoroutineScope,
    private val comicRepository: ComicRepository,
) : StateHolder<ComicState, ComicUserAction> {

    private val _showDialogFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _latestComicFlow = comicRepository.getLatest()
    override val state = viewModelScope.launchMolecule(RecompositionMode.Immediate) {
        Presenter(latestComicFlow = _latestComicFlow, showDialogFlow = _showDialogFlow)
    }

    init {
        viewModelScope.launch {
            val comic = state.filterIsInstance<ComicState.Data>().first()
            comicRepository.markAsSeen(comicNum = comic.comicNumber)
        }
    }

    override fun handle(action: ComicUserAction) {
        when (action) {
            is ComicUserAction.ToggleFavorite -> {
                viewModelScope.launch {
                    comicRepository.toggleFavorite(action.comicNum, action.isFavorite)
                }
            }

            is ComicUserAction.ImageClicked -> {
                _showDialogFlow.update { true }
            }

            is ComicUserAction.OverlayClicked -> {
                _showDialogFlow.update { false }
            }

            ComicUserAction.BackButtonClicked -> {
                TODO()
            }

            is ComicUserAction.NavigateToComic -> TODO()

        }
    }

    @Composable
    private fun Presenter(
        latestComicFlow: Flow<Comic>,
        showDialogFlow: Flow<Boolean>
    ): ComicState {
        val latestComic by latestComicFlow.collectAsState(initial = null)
        val showDialog by showDialogFlow.collectAsState(initial = false)

        return latestComic?.let { comic ->
            ComicState.Data(
                comicNumber = comic.number,
                comicTitle = comic.title,
                imageUrl = comic.imageUrl,
                altText = comic.altText,
                imageDescription = comic.transcript,
                permalink = comic.permalink,
                explainXckdPermalink = comic.explainXkcdPermalink,
                isFavorite = comic.isFavorite,
                showDialog = showDialog,
                nextComic = null,
                previousComic = null,
                firstComic = 0,
                lastComic = 0
            )
        } ?: ComicState.Loading(comicNumber = null)
    }
}
