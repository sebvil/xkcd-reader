package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComicStateHolder(
    private val delegate: ComicDelegate,
    private val comicProps: StateFlow<ComicProps>,
    private val viewModelScope: CoroutineScope,
    private val comicRepository: ComicRepository,
) : StateHolder<ComicState, ComicUserAction> {

    private val _showDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _comic = comicProps.flatMapLatest {
        it.comicNumber?.let { comicNumber ->
            comicRepository.getComic(comicNumber)
        } ?: flowOf(null)
    }

    override val state = viewModelScope.launchMolecule(mode = RecompositionMode.Immediate) {
        Presenter(propsFlow = comicProps, comicFlow = _comic, showDialogFlow = _showDialog)
    }

    init {
        comicProps.mapNotNull { it.comicNumber }.onEach {
            comicRepository.markAsSeen(it)
        }.launchIn(viewModelScope)
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

            is ComicUserAction.NavigateToComic -> {
                delegate.showComic(action.comicNumber)
            }
        }
    }

    @Composable
    private fun Presenter(
        propsFlow: StateFlow<ComicProps>,
        comicFlow: Flow<Comic?>,
        showDialogFlow: Flow<Boolean>
    ): ComicState {
        val props by propsFlow.collectAsState()
        val comic by comicFlow.collectAsState(initial = null)
        val showDialog by showDialogFlow.collectAsState(initial = false)
        var shownComics by remember {
            mutableStateOf(props.shownComics)
        }

        LaunchedEffect(key1 = props.isShowingComic, key2 = props.shownComics) {
            if (!props.isShowingComic) {
                shownComics = props.shownComics
            }
        }
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
                navigationState = NavigationState.fromComicAndShownComics(
                    comicNumber = props.comicNumber,
                    shownComics = shownComics,
                ),
            )
        } ?: ComicState.Loading(
            comicNumber = props.comicNumber ?: 0,
            navigationState = NavigationState.fromComicAndShownComics(
                comicNumber = props.comicNumber,
                shownComics = shownComics,
            ),
        )
    }
}
