package com.colibrez.xkcdreader.android.ui.core.mvvm

import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import kotlinx.coroutines.flow.StateFlow

interface StateHolder<State: UiState, Action: UserAction> {
    val state: StateFlow<State>
    fun handle(action: Action)
}