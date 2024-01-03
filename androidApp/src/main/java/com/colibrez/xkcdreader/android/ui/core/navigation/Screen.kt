package com.colibrez.xkcdreader.android.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.colibrez.xkcdreader.android.ui.core.mvvm.BaseViewModel
import com.colibrez.xkcdreader.android.ui.core.mvvm.UiState
import com.colibrez.xkcdreader.android.ui.core.mvvm.UserAction
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
fun <State : UiState, Action : UserAction> Screen(
    viewModel: BaseViewModel<State, Action>,
    navigator: DestinationsNavigator,
    layout: @Composable (state: State, handleUserAction: (Action) -> Unit) -> Unit) {

    val navigationState by viewModel.navigationState.collectAsState()

    LaunchedEffect(key1 = navigationState) {
        when (val navState = navigationState) {
            is NavigationState.NavigateUp -> {
                navigator.navigateUp()
            }
            is NavigationState.ShowScreen -> {
                navigator.navigate(navState.arguments.direction, onlyIfResumed = true)
                viewModel.resetNavigationState()
            }
            null -> Unit
        }
    }

    val state by viewModel.state.collectAsState()

    layout(state, viewModel::handle)

}