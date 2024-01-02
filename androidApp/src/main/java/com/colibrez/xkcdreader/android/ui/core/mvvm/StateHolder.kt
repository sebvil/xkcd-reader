package com.colibrez.xkcdreader.android.ui.core.mvvm

import com.colibrez.xkcdreader.android.ui.core.navigation.ScreenArguments
import kotlinx.coroutines.flow.StateFlow

interface StateHolder<State: UiState, Action: UserAction> {
    val state: StateFlow<State>
    val navigationState: StateFlow<ScreenArguments<*>?>
    fun handle(action: Action)
    fun resetNavigationState()
}