package com.colibrez.xkcdreader.android.ui.core.navigation

sealed interface NavigationState {
    data class ShowScreen(val arguments: ScreenArguments<*>) : NavigationState
    data object NavigateUp : NavigationState
}