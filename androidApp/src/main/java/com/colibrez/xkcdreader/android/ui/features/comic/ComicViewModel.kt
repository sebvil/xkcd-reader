package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.android.ui.features.navArgs
import com.colibrez.xkcdreader.data.repository.ComicRepository

class ComicViewModel(
    private val comicRepository: ComicRepository,
    arguments: ComicScreenArguments,
) : BaseViewModel<ComicState, ComicUserAction>() {

    override val stateHolder: ComicStateHolder = ComicStateHolder(
        arguments = arguments,
        viewModelScope = viewModelScope,
        comicRepository = comicRepository,
    )

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
                arguments = handle.navArgs(),
            )
        }
    }
}
