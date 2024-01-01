package com.colibrez.xkcdreader.android.ui.core.mvvm

import kotlinx.coroutines.flow.StateFlow

interface StateHolder<State: UiState, Action: UserAction> {
    val state: StateFlow<State>
    fun handle(action: Action)
}