package com.colibrez.xkcdreader.android.ui.core.mvvm

import android.util.Log
import androidx.lifecycle.ViewModel
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<State : UiState, Action : UserAction> : NavigationSupportedStateHolder<State, Action>,
    ViewModel() {
    protected abstract val stateHolder: NavigationSupportedStateHolder<State, Action>
    final override val state by lazy {
        stateHolder.state
    }

    final override val navigationState: StateFlow<NavigationState?> by lazy {
        stateHolder.navigationState
    }

    final override fun resetNavigationState() {
        stateHolder.resetNavigationState()
    }

    final override fun handle(action: Action) {
        Log.i("ViewModel", "handle($action)")
        stateHolder.handle(action)
    }
}
