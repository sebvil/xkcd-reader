package com.colibrez.xkcdreader.android.ui

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.data.repository.ComicRepository
import com.colibrez.xkcdreader.model.Comic
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ComicUserAction {
    data class ToggleFavorite(val comicNum: Long, val isFavorite: Boolean) : ComicUserAction
}

class ComicViewModel(private val comicRepository: ComicRepository, comicNum: Long) : ViewModel() {

    val state: StateFlow<Comic?> = comicRepository.getComic(comicNum).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    init {
        viewModelScope.launch {
            comicRepository.markAsSeen(comicNum)
        }
    }

    fun handle(action: ComicUserAction) {
        when (action) {
            is ComicUserAction.ToggleFavorite -> {
                viewModelScope.launch {
                    comicRepository.toggleFavorite(action.comicNum, action.isFavorite)
                }
            }
        }
    }

    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicRepository: ComicRepository,
        private val num: Long
    ) : AbstractSavedStateViewModelFactory(owner, null) {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return ComicViewModel(comicRepository, num) as T
        }
    }

}