package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseStateHolder
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComicStateHolder(
    arguments: ComicScreenArguments,
    private val viewModelScope: CoroutineScope,
    private val comicRepository: ComicRepository,
) : BaseStateHolder<ComicState, ComicUserAction>() {

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
                setNavigationState(NavigationState.NavigateUp)
            }
        }
    }

    @Composable
    private fun Presenter(
        arguments: ComicScreenArguments,
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
                isFavorite = it.isFavorite,
                showDialog = showDialog,
            )
        } ?: ComicState.Loading(
            comicNumber = arguments.comicNumber,
            comicTitle = arguments.comicTitle,
        )
    }
}
