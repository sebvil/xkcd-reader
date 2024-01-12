package com.colibrez.xkcdreader.android.ui.core.mvvm

import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseStateHolder<State : UiState, Action : UserAction> : NavigationSupportedStateHolder<State, Action> {
    private val _navigationState: MutableStateFlow<NavigationState?> = MutableStateFlow(null)

    final override val navigationState: StateFlow<NavigationState?>
        get() = _navigationState.asStateFlow()

    final override fun resetNavigationState() {
        _navigationState.update { null }
    }

    protected fun setNavigationState(newNavigationState: NavigationState) {
        _navigationState.update { newNavigationState }
    }
}
