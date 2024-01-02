package com.colibrez.xkcdreader.android.ui.core.mvvm

import androidx.lifecycle.ViewModel
import com.colibrez.xkcdreader.android.ui.core.navigation.ScreenArguments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State : UiState, Action : UserAction> : StateHolder<State, Action>, ViewModel() {
    private val _navigationState: MutableStateFlow<ScreenArguments<*>?> = MutableStateFlow(null)

    final override val navigationState: StateFlow<ScreenArguments<*>?>
        get() = _navigationState.asStateFlow()

    final override fun resetNavigationState() {
        _navigationState.update { null }
    }

    protected fun navigateTo(arguments: ScreenArguments<*>) {
        _navigationState.update { arguments }
    }

}