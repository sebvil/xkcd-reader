package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction
import com.colibrez.xkcdreader.data.repository.ComicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
) : BaseViewModel<ComicState, ComicUserAction>() {

    private val showDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val state =
        combine(comicRepository.getComic(comicNum), showDialog) { comic, showDialog ->
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
            started = SharingStarted.WhileSubscribed(),
            initialValue = ComicState.Loading(comicNum, comicTitle)
        )


    init {
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
                showDialog.update { true }
            }

            is ComicUserAction.HideDialog -> {
                showDialog.update { false }
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