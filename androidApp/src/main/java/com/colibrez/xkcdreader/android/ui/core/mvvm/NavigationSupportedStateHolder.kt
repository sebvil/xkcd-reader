package com.colibrez.xkcdreader.android.ui.core.mvvm

interface NavigationSupportedStateHolder<State : UiState, Action : UserAction> : StateHolder<State, Action>,
    NavigationStateHolder