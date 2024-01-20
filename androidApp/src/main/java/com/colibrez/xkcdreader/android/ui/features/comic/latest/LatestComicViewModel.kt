package com.colibrez.xkcdreader.android.ui.features.comic.latest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.android.ui.features.comic.ComicState
import com.colibrez.xkcdreader.android.ui.features.comic.ComicUserAction
import com.colibrez.xkcdreader.data.repository.ComicRepository

class LatestComicViewModel(
    private val comicRepository: ComicRepository,
) : BaseViewModel<ComicState, ComicUserAction>() {

    override val stateHolder: LatestComicStateHolder = LatestComicStateHolder(
        viewModelScope = viewModelScope,
        comicRepository = comicRepository,
    )

    class Factory(
        owner: SavedStateRegistryOwner,
        private val comicRepository: ComicRepository,
    ) : BaseViewModelFactory<LatestComicViewModel>(owner) {

        override fun create(
            key: String,
            handle: SavedStateHandle
        ): LatestComicViewModel {
            return LatestComicViewModel(
                comicRepository = comicRepository,
            )
        }
    }
}
