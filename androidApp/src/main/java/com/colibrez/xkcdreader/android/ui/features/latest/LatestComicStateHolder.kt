package com.colibrez.xkcdreader.android.ui.features.latest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseStateHolder
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
) : BaseStateHolder<LatestComicState, LatestComicUserAction>() {

    private val _showDialogFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _latestComicFlow = comicRepository.getLatest()
    override val state = viewModelScope.launchMolecule(RecompositionMode.Immediate) {
        Presenter(latestComicFlow = _latestComicFlow, showDialogFlow = _showDialogFlow)
    }

    init {
        viewModelScope.launch {
            val comic = state.filterIsInstance<LatestComicState.Data>().first()
            comicRepository.markAsSeen(comicNum = comic.comicNumber)
        }
    }

    override fun handle(action: LatestComicUserAction) {
        when (action) {
            is LatestComicUserAction.ToggleFavorite -> {
                viewModelScope.launch {
                    comicRepository.toggleFavorite(action.comicNum, action.isFavorite)
                }
            }

            is LatestComicUserAction.ImageClicked -> {
                _showDialogFlow.update { true }
            }

            is LatestComicUserAction.OverlayClicked -> {
                _showDialogFlow.update { false }
            }
        }
    }

    @Composable
    private fun Presenter(
        latestComicFlow: Flow<Comic>,
        showDialogFlow: Flow<Boolean>
    ): LatestComicState {
        val latestComic by latestComicFlow.collectAsState(initial = null)
        val showDialog by showDialogFlow.collectAsState(initial = false)

        return latestComic?.let { comic ->
            LatestComicState.Data(
                comicNumber = comic.number,
                comicTitle = comic.title,
                imageUrl = comic.imageUrl,
                altText = comic.altText,
                imageDescription = comic.transcript,
                permalink = comic.permalink,
                isFavorite = comic.isFavorite,
                showDialog = showDialog,
            )
        } ?: LatestComicState.Loading
    }
}
