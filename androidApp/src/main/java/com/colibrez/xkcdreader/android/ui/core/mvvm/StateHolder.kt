package com.colibrez.xkcdreader.android.ui.core.mvvm

import kotlinx.coroutines.flow.StateFlow

interface StateHolder<State : UiState, Action : UserAction> {
    val state: StateFlow<State>
    fun handle(action: Action)

    companion object {
        const val DEFAULT_SUBSCRIPTION_TIME = 5_000L
    }
}
