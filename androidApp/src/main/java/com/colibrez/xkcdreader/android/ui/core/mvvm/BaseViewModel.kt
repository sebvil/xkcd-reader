package com.colibrez.xkcdreader.android.ui.core.mvvm

import androidx.lifecycle.ViewModel
import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import com.colibrez.xkcdreader.android.ui.core.navigation.ScreenArguments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State : UiState, Action : UserAction> : StateHolder<State, Action>, ViewModel() {
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