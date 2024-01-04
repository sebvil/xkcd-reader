package com.colibrez.xkcdreader.android.ui.features.comic

import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseStateHolder
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.data.repository.ComicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ComicStateHolder(
    arguments: ComicScreenArguments,
    private val viewModelScope: CoroutineScope,
    private val comicRepository: ComicRepository,
) : BaseStateHolder<ComicState, ComicUserAction>() {

    private val showDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val state =
        combine(comicRepository.getComic(arguments.comicNumber), showDialog) { comic, showDialog ->
            ComicState.Data(
                comicNumber = comic.number,
                comicTitle = comic.title,
                imageUrl = comic.imageUrl,
                altText = comic.altText,
                imageDescription = comic.transcript,
                permalink = comic.permalink,
                isFavorite = comic.isFavorite,
                showDialog = showDialog
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = ComicState.Loading(
                comicNumber = arguments.comicNumber,
                comicTitle = arguments.comicTitle
            )
        )


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
                showDialog.update { true }
            }

            is ComicUserAction.OverlayClicked -> {
                showDialog.update { false }
            }
            is ComicUserAction.BackButtonClicked -> {
                setNavigationState(NavigationState.NavigateUp)
            }
        }
    }
}