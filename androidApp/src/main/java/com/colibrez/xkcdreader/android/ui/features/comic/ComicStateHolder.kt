package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComicStateHolder(
    arguments: ComicArguments,
    private val delegate: ComicDelegate,
    private val viewModelScope: CoroutineScope,
    private val comicRepository: ComicRepository,
) : StateHolder<ComicState, ComicUserAction> {

    private val _showDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _comic = comicRepository.getComic(arguments.comicNumber)

    override val state = viewModelScope.launchMolecule(mode = RecompositionMode.Immediate) {
        Presenter(arguments = arguments, comicFlow = _comic, showDialogFlow = _showDialog)
    }

    init {
        viewModelScope.launch {
            comicRepository.markAsSeen(arguments.comicNumber)
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
                _showDialog.update { true }
            }

            is ComicUserAction.OverlayClicked -> {
                _showDialog.update { false }
            }

            is ComicUserAction.BackButtonClicked -> {
                delegate.popScreen()
            }
        }
    }

    @Composable
    private fun Presenter(
        arguments: ComicArguments,
        comicFlow: Flow<Comic>,
        showDialogFlow: Flow<Boolean>
    ): ComicState {
        val comic by comicFlow.collectAsState(initial = null)
        val showDialog by showDialogFlow.collectAsState(initial = false)

        return comic?.let {
            ComicState.Data(
                comicNumber = it.number,
                comicTitle = it.title,
                imageUrl = it.imageUrl,
                altText = it.altText,
                imageDescription = it.transcript,
                permalink = it.permalink,
                explainXckdPermalink = it.explainXkcdPermalink,
                isFavorite = it.isFavorite,
                showDialog = showDialog,
            )
        } ?: ComicState.Loading(
            comicNumber = arguments.comicNumber,
        )
    }
}
