package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction
import com.colibrez.xkcdreader.data.repository.ComicRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface ComicUserAction : UserAction {
    data class ToggleFavorite(val comicNum: Long, val isFavorite: Boolean) : ComicUserAction
    data object ShowDialog : ComicUserAction
    data object HideDialog : ComicUserAction
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


class ComicViewModel(
    private val comicRepository: ComicRepository,
    comicNum: Long,
    comicTitle: String
) :
    BaseViewModel<ComicState, ComicUserAction>(
        initialState = ComicState.Loading(
            comicNumber = comicNum,
            comicTitle = comicTitle
        )
    ) {

    init {
        comicRepository.getComic(comicNum).onEach { comic ->
            setState { comicState ->
                when (comicState) {
                    is ComicState.Data -> {
                        comicState.copy(
                            comicNumber = comic.num,
                            comicTitle = comic.title,
                            imageUrl = comic.img,
                            altText = comic.alt,
                            imageDescription = comic.transcript,
                            permalink = comic.permalink,
                            isFavorite = comic.isFavorite
                        )
                    }

                    is ComicState.Loading -> {
                        ComicState.Data(
                            comicNumber = comic.num,
                            comicTitle = comic.title,
                            imageUrl = comic.img,
                            altText = comic.alt,
                            imageDescription = comic.transcript,
                            permalink = comic.permalink,
                            isFavorite = comic.isFavorite,
                            showDialog = false
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
        viewModelScope.launch {
            comicRepository.markAsSeen(comicNum)
        }
    }

    override fun handle(action: ComicUserAction) {
        when (action) {
            is ComicUserAction.ToggleFavorite -> {
                viewModelScope.launch {
                    comicRepository.toggleFavorite(action.comicNum, action.isFavorite)
                }
            }

            is ComicUserAction.ShowDialog -> {
                setState {
                    when (it) {
                        is ComicState.Data -> it.copy(showDialog = true)
                        is ComicState.Loading -> it
                    }
                }
            }

            is ComicUserAction.HideDialog -> {
                setState {
                    when (it) {
                        is ComicState.Data -> it.copy(showDialog = false)
                        is ComicState.Loading -> it
                    }
                }
            }
        }
    }

    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicRepository: ComicRepository,
        private val comicNumber: Long,
        private val comicTitle: String
    ) : AbstractSavedStateViewModelFactory(owner, null) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return ComicViewModel(
                comicRepository = comicRepository,
                comicNum = comicNumber,
                comicTitle = comicTitle
            ) as T
        }
    }
}