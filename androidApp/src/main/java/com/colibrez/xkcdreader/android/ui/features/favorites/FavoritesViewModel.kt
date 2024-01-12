package com.colibrez.xkcdreader.android.ui.features.favorites

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.data.repository.ComicRepository

class FavoritesViewModel(
    comicRepository: ComicRepository
) : BaseViewModel<FavoritesState, FavoritesUserAction>() {

    override val stateHolder: FavoritesStateHolder = FavoritesStateHolder(
        viewModelScope = viewModelScope,
        comicRepository = comicRepository,
    )

    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicRepository: ComicRepository,
    ) : BaseViewModelFactory<FavoritesViewModel>(owner) {

        override fun create(
            key: String,
            handle: SavedStateHandle
        ): FavoritesViewModel {
            return FavoritesViewModel(
                comicRepository = comicRepository,
            )
        }
    }
}
