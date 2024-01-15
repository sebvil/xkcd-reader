package com.colibrez.xkcdreader.android.ui.core.mvvm

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeStateHolder<State : UiState, Action : UserAction>(initialState: State) :
    StateHolder<State, Action> {

    val stateFlow = MutableStateFlow(initialState)
    override val state: StateFlow<State>
        get() = stateFlow.asStateFlow()

    private val _handleInvocations: MutableList<Action> = mutableListOf()
    val handleInvocations: List<Action>
        get() = _handleInvocations.toList()

    override fun handle(action: Action) {
        _handleInvocations.add(action)
    }
}
