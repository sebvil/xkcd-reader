package com.colibrez.xkcdreader.android.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.colibrez.xkcdreader.android.ui.features.navigation.NavigationBarDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
data class MainState(
    val tabs: List<NavigationBarDestination>,
    val currentTabIndex: Int
)

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        MainState(
            tabs = NavigationBarDestination.entries,
            currentTabIndex = 0,
        ),
    )

    val state: StateFlow<MainState>
        get() = _state.asStateFlow()

    fun onTabSelected(index: Int) {
        _state.update { it.copy(currentTabIndex = index) }
    }
}
