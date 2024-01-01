package com.colibrez.xkcdreader.android.ui.core.mvvm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<State : UiState, Action : UserAction>(initialState: State) :
    StateHolder<State, Action>, ViewModel() {
    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)


    final override val state: StateFlow<State>
        get() = _state.asStateFlow()


    fun setState(transform: (State) -> State) {
        _state.update(transform)
    }
}