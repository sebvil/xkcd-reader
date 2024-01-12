package com.colibrez.xkcdreader.android.ui.core.mvvm

import com.colibrez.xkcdreader.android.ui.core.navigation.NavigationState
import kotlinx.coroutines.flow.StateFlow

interface NavigationStateHolder {
    val navigationState: StateFlow<NavigationState?>
    fun resetNavigationState()
}
