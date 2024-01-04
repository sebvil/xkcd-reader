package com.colibrez.xkcdreader.android.ui.core.mvvm

import androidx.lifecycle.ViewModel
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.android.ui.core.navigation.ScreenArguments
import com.colibrez.xkcdreader.android.ui.features.comic.ComicUserAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State : UiState, Action : UserAction> : StateHolder<State, Action>,
    ViewModel() {
    protected abstract val stateHolder: StateHolder<State, Action>
    final override val state by lazy {
        stateHolder.state
    }

    final override val navigationState: StateFlow<NavigationState?> by lazy {
        stateHolder.navigationState
    }

    final override fun resetNavigationState() {
        stateHolder.navigationState
    }

    final override fun handle(action: Action) = stateHolder.handle(action)

}