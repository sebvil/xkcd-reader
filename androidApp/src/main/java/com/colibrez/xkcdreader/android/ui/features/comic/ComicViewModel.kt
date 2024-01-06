package com.colibrez.xkcdreader.android.ui.features.comic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModelFactory
import com.colibrez.xkcdreader.android.ui.core.mvvm.StateHolder
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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class ComicViewModel(
    private val comicRepository: ComicRepository,
    arguments: ComicScreenArguments,
) : BaseViewModel<ComicState, ComicUserAction>() {

    override val stateHolder: ComicStateHolder = ComicStateHolder(
        arguments = arguments,
        viewModelScope = viewModelScope,
        comicRepository = comicRepository
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
                arguments = handle.navArgs()
            )
        }
    }
}