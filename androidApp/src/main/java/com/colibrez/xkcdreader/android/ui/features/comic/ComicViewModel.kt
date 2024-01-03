package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.android.ui.core.navigation.ScreenArguments
import com.colibrez.xkcdreader.android.ui.features.destinations.ComicScreenDestination
import com.colibrez.xkcdreader.android.ui.features.navArgs
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.ramcosta.composedestinations.spec.Direction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

sealed interface ComicUserAction : UserAction {
    data class ToggleFavorite(val comicNum: Long, val isFavorite: Boolean) : ComicUserAction
    data object ImageClicked : ComicUserAction
    data object OverlayClicked : ComicUserAction
    data object BackButtonClicked : ComicUserAction
}

sealed interface ComicState : UiState {
    val comicNumber: Long
    val comicTitle: String

    data class Data(
        override val comicNumber: Long,
        override val comicTitle: String,
        val imageUrl: String,
        val altText: String,
        val imageDescription: String,
        val permalink: String,
        val isFavorite: Boolean,
        val showDialog: Boolean
    ) : ComicState

    data class Loading(override val comicNumber: Long, override val comicTitle: String) : ComicState
}

@Serializable
data class ComicScreenArguments(val comicNumber: Long, val comicTitle: String) :
    ScreenArguments<ComicScreenArguments> {
    override val direction: Direction
        get() = ComicScreenDestination(this)
}

class ComicViewModel(
    private val comicRepository: ComicRepository,
    arguments: ComicScreenArguments,
) : BaseViewModel<ComicState, ComicUserAction>() {

    private val showDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val state =
        combine(comicRepository.getComic(arguments.comicNumber), showDialog) { comic, showDialog ->
            ComicState.Data(
                comicNumber = comic.num,
                comicTitle = comic.title,
                imageUrl = comic.img,
                altText = comic.alt,
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

    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicRepository: ComicRepository,
    ) : BaseViewModelFactory<ComicViewModel>(owner) {

        override fun create(
            key: String,
            handle: SavedStateHandle
        ): ComicViewModel {
            return ComicViewModel(
                comicRepository = comicRepository,
                arguments = handle.navArgs()
            )
        }
    }
}